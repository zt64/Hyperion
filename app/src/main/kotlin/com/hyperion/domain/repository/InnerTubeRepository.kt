package com.hyperion.domain.repository

import com.hyperion.domain.mapper.toDomain
import com.hyperion.domain.model.*
import com.hyperion.network.dto.ApiFormat
import com.hyperion.network.dto.ApiNext
import com.hyperion.network.dto.ApiText
import com.hyperion.network.service.InnerTubeService
import com.hyperion.network.service.RYDService
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class InnerTubeRepository @Inject constructor(
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
            continuation = section.continuations.findLast { it.nextContinuationData != null }?.nextContinuationData?.continuation,
            videos = section.contents!!.mapNotNull {
                it.itemSectionRenderer?.contents?.single()?.elementRenderer?.newElement?.type?.componentType?.model?.videoWithContextModel?.videoWithContextData?.toDomain()
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
            continuation = section.continuations.first().nextContinuationData?.continuation,
            items = section.contents.filter { it.itemSectionRenderer != null }.flatMap {
                it.itemSectionRenderer!!.contents.mapNotNull { content ->
                    when {
                        content.compactVideoRenderer != null -> {
                            val video = content.compactVideoRenderer

                            DomainSearch.Result.Video(
                                id = video.videoId,
                                title = video.title.toString(),
                                subtitle = buildString {
                                    append("${video.shortBylineText.runs.first().text} - ")
                                    video.shortViewCountText?.let { viewCount -> append("$viewCount - ") }
                                    append("${video.publishedTimeText?.toString()}")
                                },
                                timestamp = video.lengthText?.toString(),
                                thumbnailUrl = video.thumbnail.thumbnails.last().url,
                                author = DomainChannelPartial(
                                    id = video.shortBylineText.runs.first().navigationEndpoint.browseEndpoint.browseId,
                                    avatarUrl = video.channelThumbnail.thumbnails.first().url
                                )
                            )
                        }
                        content.compactChannelRenderer != null -> {
                            val channel = content.compactChannelRenderer

                            DomainSearch.Result.Channel(
                                id = channel.channelId,
                                name = channel.displayName.toString(),
                                thumbnailUrl = "https://${channel.thumbnail.thumbnails.last().url}",
                                subscriptionsText = channel.subscriberCountText.runs.first().text,
                                videoCountText = channel.videoCountText.runs.joinToString(
                                    separator = "",
                                    transform = ApiText.TextRun::text
                                )
                            )
                        }
                        content.compactPlaylistRenderer != null -> {
                            val playlist = content.compactPlaylistRenderer

                            DomainSearch.Result.Playlist(
                                id = playlist.playlistId,
                                title = playlist.title.toString(),
                                thumbnailUrl = playlist.thumbnail.thumbnails.last().url,
                                videoCountText = playlist.videoCountShortText.toString(),
                                channelName = playlist.shortBylineText.toString()
                            )
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

        return DomainNext(
            viewCount = slimVideoMetadataSectionRenderer.contents[0].elementRenderer.newElement.type.componentType.model.videoMetadataModel!!.videoMetadata.subtitleData.viewCount.content,
            uploadDate = slimVideoMetadataSectionRenderer.contents[0].elementRenderer.newElement.type.componentType.model.videoMetadataModel!!.videoMetadata.subtitleData.dateA11yLabel,
            channelAvatar = slimVideoMetadataSectionRenderer.contents[2].elementRenderer.newElement.type.componentType.model.channelBarModel!!.videoChannelBarData.avatar.image.sources[0].url,
            likesText = slimVideoMetadataSectionRenderer.contents[1].elementRenderer.newElement.type.componentType.model.videoActionBarModel!!.buttons[0].likeButton!!.buttonData.defaultButton.title,
            subscribersText = slimVideoMetadataSectionRenderer.contents[2].elementRenderer.newElement.type.componentType.model.channelBarModel!!.videoChannelBarData.subtitle,
            comments = DomainNext.Comments(
                continuation = null,
                comments = emptyList()
            ),
            relatedVideos = DomainNext.RelatedVideos(
                continuation = next.contents.singleColumnWatchNextResults.results.results.continuations.first().nextContinuationData!!.continuation,
                videos = next.contents.singleColumnWatchNextResults.results.results.contents
                    .filterIsInstance<ApiNext.Contents.SingleColumnWatchNextResults.Results.Results.RelatedItemsRenderer>()
                    .flatMap {
                        it.contents.mapNotNull { (renderer) ->
                            renderer.newElement.type.componentType.model.videoWithContextModel?.videoWithContextData?.toDomain()
                        }
                    }
            )
        )
    }

    suspend fun getRelatedVideos(videoId: String, continuation: String): DomainNext.RelatedVideos {
        val next = service.getNext(videoId, continuation)

        return DomainNext.RelatedVideos(
            continuation = next.continuationContents!!.sectionListContinuation.continuations.first().nextContinuationData?.continuation,
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
            dislikes = rydService.getVotes(id).dislikes,
            streams = player.streamingData.formats.map(ApiFormat::toDomain),
            author = DomainChannelPartial(
                id = player.videoDetails.channelId,
                name = player.videoDetails.author,
                avatarUrl = next.channelAvatar,
                subscriberText = next.subscribersText
            )
        )
    }
}