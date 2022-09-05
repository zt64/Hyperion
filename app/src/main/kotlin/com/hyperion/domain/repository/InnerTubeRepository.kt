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
        val section = if (continuation == null) {
            service.getTrending().contents.browseResultsRenderer.tabs[0].tabRenderer.content!!.sectionListRenderer
        } else {
            service.getTrending(continuation).continuationContents.sectionListContinuation
        }

        return DomainTrending(
            continuation = section.continuations.filterIsInstance<ApiContinuation.Next>().singleOrNull()?.continuation,
            videos = section.contents
                .mapNotNull { it.itemSectionRenderer }
                .mapNotNull { renderer ->
                    renderer.contents.single().elementRenderer?.model?.videoWithContextModel?.videoWithContextData?.toDomain()
                }
        )
    }

    suspend fun getRecommendations(continuation: String? = null): DomainRecommended {
        val section = if (continuation == null) {
            service.getRecommendations().contents.browseResultsRenderer.tabs[0].tabRenderer.content!!.sectionListRenderer
        } else {
            service.getRecommendations(continuation).continuationContents.sectionListContinuation
        }

        return DomainRecommended(
            continuation = section.continuations.filterIsInstance<ApiContinuation.Next>().singleOrNull()?.continuation,
            videos = section.contents
                .mapNotNull { it.itemSectionRenderer }
                .mapNotNull { renderer ->
                    renderer.contents.first().elementRenderer?.model?.videoWithContextModel?.videoWithContextData?.toDomain()
                }
        )
    }

    suspend fun getSubscriptions() = service.getSubscriptions()

    suspend fun getSearchSuggestions(query: String) = service.getSearchSuggestions(query).jsonArray[1].jsonArray
        .map { it.jsonArray[0].jsonPrimitive.content }

    suspend fun getSearchResults(query: String, continuation: String? = null): DomainSearch {
        val section = if (continuation != null) {
            service.getSearchResults(query, continuation).continuationContents.sectionListContinuation
        } else {
            service.getSearchResults(query).contents.sectionListRenderer
        }

        return DomainSearch(
            continuation = section.continuations.filterIsInstance<ApiContinuation.Next>().singleOrNull()?.continuation,
            items = section.contents
                .mapNotNull { it.itemSectionRenderer }
                .flatMap {
                    it.contents.mapNotNull { renderer ->
                        when (renderer) {
                            is ApiSearch.Renderer.CompactChannel -> {
                                DomainSearch.Result.Channel(
                                    id = renderer.compactChannelRenderer.channelId,
                                    name = renderer.compactChannelRenderer.title.toString(),
                                    thumbnailUrl = "https:${renderer.compactChannelRenderer.thumbnail.thumbnails.last().url}",
                                    subscriptionsText = renderer.compactChannelRenderer.subscriberCountText?.toString(),
                                    videoCountText = renderer.compactChannelRenderer.videoCountText?.toString(),
                                )
                            }
                            is ApiSearch.Renderer.Element -> {
                                when (val model = renderer.elementRenderer.model) {
                                    is ApiSearch.VideoWithContextSlots -> {
                                        val (onTap, videoData) = model.videoWithContextSlotsModel.videoWithContextData

                                        if (videoData.metadata.isPlaylistMix) return@mapNotNull null

                                        DomainSearch.Result.Video(
                                            id = onTap.innertubeCommand.watchEndpoint.videoId!!,
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

    suspend fun getChannel(id: String): DomainChannel {
        val channel = service.getChannel(id)
        val channelHeaderModal = channel.header.channelMobileHeaderRenderer.channelHeader.elementRenderer.model.channelHeaderModel

        val channelProfile = channelHeaderModal.channelProfile
        val avatars = channelProfile.avatarData.avatar.image.sources
        val banners = channelHeaderModal.channelBanner?.image?.sources

        val tabRenderer = channel.contents.browseResultsRenderer.tabs.first().tabRenderer

        return DomainChannel(
            id = tabRenderer.endpoint.browseEndpoint.browseId,
            name = channelProfile.title,
            description = channelProfile.descriptionPreview.description,
            subscriberText = channelHeaderModal.channelProfile.metadata.subscriberCountText,
            avatar = avatars.first().url,
            banner = banners?.lastOrNull()?.url,
            videos = tabRenderer.content.sectionListRenderer.contents
                .mapNotNull { it.shelfRenderer }
                .mapNotNull { (shelfRenderer) ->
                    shelfRenderer.horizontalListRenderer?.items
                        ?.firstOrNull()?.elementRenderer?.model?.gridVideoModel
                        ?.let { (videoData, onTap) ->
                            DomainVideoPartial(
                                id = onTap.innertubeCommand.watchEndpoint?.videoId ?: "",
                                title = videoData.metadata.title,
                                subtitle = videoData.metadata.metadataDetails,
                                timestamp = videoData.thumbnail.timestampText
                            )
                        }
                }
        )
    }

    suspend fun getNext(videoId: String): DomainNext {
        val next = service.getNext(videoId)

        val slimVideoMetadataSectionRenderer = next.contents.singleColumnWatchNextResults.results.results.contents
            .filterIsInstance<ApiNext.Renderer.SlimVideoMetadataSection>()
            .single()

        val videoMetadata = slimVideoMetadataSectionRenderer.contents[0].elementRenderer.model.videoMetadataModel!!.videoMetadata
        val videoActionBarButtons = slimVideoMetadataSectionRenderer.contents[1].elementRenderer.model.videoActionBarModel!!.buttons
        val channelBar = slimVideoMetadataSectionRenderer.contents[2].elementRenderer.model.channelBarModel!!.videoChannelBarData

        return DomainNext(
            viewCount = videoMetadata.subtitleData.viewCount.content,
            uploadDate = videoMetadata.subtitleData.date.content,
            channelAvatarUrl = channelBar.avatar.image.sources.first().url,
            likesText = videoActionBarButtons
                .filterIsInstance<ApiNext.Button.LikeButton>()
                .single().buttonData.defaultButton.title,
            subscribersText = channelBar.subtitle,
            comments = DomainNext.Comments(
                continuation = null,
                comments = emptyList()
            ),
            relatedVideos = DomainNext.RelatedVideos(
                continuation = next.contents.singleColumnWatchNextResults.results.results.continuations
                    .filterIsInstance<ApiContinuation.Next>()
                    .singleOrNull()?.continuation,
                videos = next.contents.singleColumnWatchNextResults.results.results.contents
                    .filterIsInstance<ApiNext.Renderer.ItemSection.RelatedItems>()
                    .map { it.contents }
                    .mapNotNull { (renderer) ->
                        renderer.elementRenderer.model.videoWithContextModel?.videoWithContextData?.toDomain()
                    }
            ),
            badges = videoMetadata.badgesData.map { it.label },
        )
    }

    suspend fun getRelatedVideos(videoId: String, continuation: String): DomainNext.RelatedVideos {
        val next = service.getNext(videoId, continuation)

        return DomainNext.RelatedVideos(
            continuation = next.continuationContents.sectionListContinuation.continuations
                .filterIsInstance<ApiContinuation.Next>()
                .singleOrNull()
                ?.continuation,
            videos = next.continuationContents.sectionListContinuation.contents.flatMap { (itemSectionRenderer) ->
                itemSectionRenderer.contents.mapNotNull { (elementRenderer) ->
                    elementRenderer.model.videoWithContextModel?.videoWithContextData?.toDomain()
                }
            }
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
            streams = player.streamingData.adaptiveFormats.map(ApiFormat::toDomain),
            author = DomainChannelPartial(
                id = player.videoDetails.channelId,
                name = player.videoDetails.author,
                avatarUrl = next.channelAvatarUrl,
                subscriberText = next.subscribersText
            ),
            badges = next.badges
        )
    }
}