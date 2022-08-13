package com.hyperion.domain.repository

import android.icu.text.CompactDecimalFormat
import android.os.Build
import com.hyperion.domain.mapper.toDomain
import com.hyperion.domain.model.*
import com.hyperion.network.dto.ApiContinuation
import com.hyperion.network.dto.ApiFormat
import com.hyperion.network.dto.ApiNext
import com.hyperion.network.dto.ApiSearch
import com.hyperion.network.service.InnerTubeService
import com.hyperion.network.service.RYDService
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.util.*

class InnerTubeRepository(
    private val service: InnerTubeService,
    private val rydService: RYDService
) {
    suspend fun getTrendingVideos(continuation: String? = null): DomainTrending {
        val trending = service.getTrending(continuation)

        val section = if (continuation == null) {
            trending.contents.singleColumnBrowseResultsRenderer.tabs[0].tabRenderer.content!!.sectionListRenderer
        } else {
            trending.continuationContents!!.sectionListContinuation
        }

        return DomainTrending(
            continuation = section.continuations.filterIsInstance<ApiContinuation.Next>().singleOrNull()?.continuation,
            videos = section.contents.mapNotNull {
                it.itemSectionRenderer?.contents?.single()?.elementRenderer?.model?.videoWithContextModel?.videoWithContextData?.toDomain()
            }
        )
    }

    suspend fun getSearchSuggestions(query: String) = service.getSearchSuggestions(query).jsonArray[1].jsonArray
        .map { it.jsonArray[0].jsonPrimitive.content }

    suspend fun getSearchResults(query: String, continuation: String? = null): DomainSearch {
        val searchResults = service.getSearchResults(query, continuation)
        val section = if (continuation == null) {
            searchResults.contents!!.sectionListRenderer
        } else {
            searchResults.continuationContents!!.sectionListContinuation
        }

        return DomainSearch(
            continuation = section.continuations.filterIsInstance<ApiContinuation.Next>().singleOrNull()?.continuation,
            items = section.contents
                .mapNotNull(ApiSearch.SectionListRenderer.Content::itemSectionRenderer)
                .flatMap {
                    it.contents.mapNotNull { renderer ->
                        when (renderer) {
                            is ApiSearch.SectionListRenderer.Content.Renderer.CompactChannel -> {
                                DomainSearch.Result.Channel(
                                    id = renderer.compactChannelRenderer.channelId,
                                    name = renderer.compactChannelRenderer.title.toString(),
                                    thumbnailUrl = "https:${renderer.compactChannelRenderer.thumbnail.thumbnails.last().url}",
                                    subscriptionsText = renderer.compactChannelRenderer.subscriberCountText?.toString(),
                                    videoCountText = renderer.compactChannelRenderer.videoCountText?.toString(),
                                )
                            }
                            is ApiSearch.SectionListRenderer.Content.Renderer.Element -> {
                                when (val model = renderer.elementRenderer.model) {
                                    is ApiSearch.VideoWithContextSlots -> {
                                        val (onTap, videoData) = model.videoWithContextSlotsModel.videoWithContextData

                                        if (videoData.metadata.isPlaylistMix) return@mapNotNull null

                                        DomainSearch.Result.Video(
                                            id = onTap.innertubeCommand.watchEndpoint!!.videoId!!,
                                            author = videoData.avatar?.let { avatar ->
                                                DomainChannelPartial(
                                                    id = avatar.endpoint.innertubeCommand.browseEndpoint.browseId,
                                                    avatarUrl = avatar.image.sources.first().url
                                                )
                                            },
                                            title = videoData.metadata.title,
                                            subtitle = videoData.metadata.metadataDetails!!,
                                            timestamp = videoData.thumbnail.timestampText
                                        )
                                    }
                                    else -> null
                                }
                            }
                            else -> null
                        }
                    }
                }
        )
    }

    suspend fun getChannel(id: String) = service.getChannel(id).toDomain()

    suspend fun getNext(videoId: String, continuation: String? = null): DomainNext {
        val next = service.getNext(videoId, continuation)

        val slimVideoMetadataSectionRenderer = next.contents?.singleColumnWatchNextResults?.results?.results?.contents?.filterIsInstance<ApiNext.Contents.SingleColumnWatchNextResults.Results.Results.SlimVideoMetadataSectionRenderer>()!!
            .first()

        val videoMetadataModel = slimVideoMetadataSectionRenderer.contents[0].elementRenderer.model.videoMetadataModel!!
        val videoActionBarModel = slimVideoMetadataSectionRenderer.contents[1].elementRenderer.model.videoActionBarModel!!
        val channelBarModel = slimVideoMetadataSectionRenderer.contents[2].elementRenderer.model.channelBarModel!!

        return DomainNext(
            viewCount = videoMetadataModel.videoMetadata.subtitleData.viewCount.content,
            uploadDate = videoMetadataModel.videoMetadata.subtitleData.date.content,
            channelAvatar = channelBarModel.videoChannelBarData.avatar.image.sources.first().url,
            likesText = videoActionBarModel.buttons
                .filterIsInstance<ApiNext.Button.LikeButton>()
                .single().buttonData.defaultButton.title,
            subscribersText = channelBarModel.videoChannelBarData.subtitle,
            comments = DomainNext.Comments(
                continuation = null,
                comments = emptyList()
            ),
            relatedVideos = DomainNext.RelatedVideos(
                continuation = next.contents.singleColumnWatchNextResults.results.results.continuations.filterIsInstance<ApiContinuation.Next>()
                    .singleOrNull()?.continuation,
                videos = next.contents.singleColumnWatchNextResults.results.results.contents
                    .filterIsInstance<ApiNext.Contents.SingleColumnWatchNextResults.Results.Results.RelatedItemsRenderer>()
                    .flatMap {
                        it.contents.mapNotNull { (renderer) ->
                            renderer.model.videoWithContextModel?.videoWithContextData?.toDomain()
                        }
                    }
            ),
            badges = videoMetadataModel.videoMetadata.badgesData.map { it.label },
        )
    }

    suspend fun getRelatedVideos(videoId: String, continuation: String): DomainNext.RelatedVideos {
        val next = service.getNext(videoId, continuation)

        return DomainNext.RelatedVideos(
            continuation = next.continuationContents!!.sectionListContinuation.continuations
                .filterIsInstance<ApiContinuation.Next>()
                .singleOrNull()
                ?.continuation,
            videos = next.continuationContents.sectionListContinuation.contents.mapNotNull { null }
        )
    }

    suspend fun getVideo(id: String): DomainVideo {
        val player = service.getPlayer(id)
        val next = getNext(id)

        return DomainVideo(
            id = player.videoDetails.videoId,
            title = player.videoDetails.title,
            viewCount = next.viewCount,
            uploadDate = next.uploadDate,
            description = player.videoDetails.shortDescription,
            likesText = next.likesText,
            dislikesText = rydService.getVotes(id).dislikes.let { dislikes ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    CompactDecimalFormat
                        .getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
                        .format(dislikes)
                } else {
                    dislikes.toString()
                }
            },
            streams = player.streamingData.formats.map(ApiFormat::toDomain),
            author = DomainChannelPartial(
                id = player.videoDetails.channelId,
                name = player.videoDetails.author,
                avatarUrl = next.channelAvatar,
                subscriberText = next.subscribersText
            ),
            badges = next.badges
        )
    }
}