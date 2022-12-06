package com.zt.innertube.domain.repository

import android.icu.text.CompactDecimalFormat
import android.os.Build
import com.zt.innertube.domain.mapper.toDomain
import com.zt.innertube.domain.model.*
import com.zt.innertube.network.dto.ApiFormat
import com.zt.innertube.network.dto.ApiNext
import com.zt.innertube.network.dto.ApiSearch
import com.zt.innertube.network.dto.next
import com.zt.innertube.network.service.InnerTubeService
import com.zt.innertube.network.service.RYDService
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.util.Locale

class InnerTubeRepository(
    private val service: InnerTubeService,
    private val rydService: RYDService
) {
    suspend fun getTrendingVideos(continuation: String? = null): DomainTrending {
        val section = if (continuation == null) {
            service.getTrending().contents.content
        } else {
            service.getTrending(continuation).continuationContents.sectionListContinuation
        }

        return DomainTrending(
            items = section.contents.mapNotNull { (renderer) ->
                renderer.contents.single().elementRenderer.model.videoWithContextModel?.videoWithContextData?.toDomain()
            },
            continuation = section.continuations.next
        )
    }

    suspend fun getRecommendations(continuation: String? = null): DomainRecommended {
        val section = if (continuation == null) {
            service.getRecommendations().contents.content
        } else {
            service.getRecommendations(continuation).continuationContents.sectionListContinuation
        }

        return DomainRecommended(
            items = section.contents.mapNotNull { (renderer) ->
                renderer.contents.first().elementRenderer?.model?.videoWithContextModel?.videoWithContextData?.toDomain()
            },
            continuation = section.continuations.next
        )
    }

    suspend fun getSubscriptions() {
        service.getSubscriptions()
    }

    suspend fun getSearchSuggestions(query: String) = service.getSearchSuggestions(query).jsonArray[1].jsonArray
        .map { it.jsonArray[0].jsonPrimitive.content }

    suspend fun getSearchResults(query: String, continuation: String? = null): DomainSearch {
        val section = if (continuation != null) {
            service.getSearchResults(query, continuation).continuationContents.sectionListContinuation
        } else {
            service.getSearchResults(query).contents.sectionListRenderer
        }

        return DomainSearch(
            items = section.contents
                .mapNotNull { it.itemSectionRenderer }
                .flatMap { (contents) ->
                    contents.mapNotNull { renderer ->
                        when (renderer) {
                            is ApiSearch.Renderer.CompactChannel -> {
                                DomainChannelPartial(
                                    id = renderer.compactChannelRenderer.channelId,
                                    name = renderer.compactChannelRenderer.title.text,
                                    thumbnailUrl = "https:${renderer.compactChannelRenderer.thumbnail.thumbnails.last().url}",
                                    subscriptionsText = renderer.compactChannelRenderer.subscriberCountText?.text,
                                    videoCountText = renderer.compactChannelRenderer.videoCountText?.text
                                )
                            }

                            is ApiSearch.Renderer.Element -> {
                                when (val model = renderer.elementRenderer.model) {
                                    is ApiSearch.VideoWithContextSlots -> {
                                        val (onTap, videoData) = model.videoWithContextSlotsModel.videoWithContextData

                                        when (videoData) {
                                            is ApiSearch.VideoWithContextSlots.Mix -> {
                                                DomainMixPartial(
                                                    id = onTap.innertubeCommand.watchEndpoint!!.videoId!!,
                                                    title = videoData.metadata.title,
                                                    subtitle = videoData.metadata.byline,
                                                    thumbnailUrl = videoData.thumbnail.image.sources.last().url
                                                )
                                            }

                                            is ApiSearch.VideoWithContextSlots.Playlist -> {
                                                DomainPlaylistPartial(
                                                    id = onTap.innertubeCommand.browseEndpoint!!.browseId,
                                                    title = videoData.metadata.title,
                                                    subtitle = videoData.metadata.byline,
                                                    videoCountText = videoData.thumbnail.videoCount,
                                                    thumbnailUrl = videoData.thumbnail.image.sources.last().url
                                                )
                                            }

                                            is ApiSearch.VideoWithContextSlots.Video -> {
                                                DomainVideoPartial(
                                                    id = onTap.innertubeCommand.watchEndpoint!!.videoId!!,
                                                    channel = videoData.avatar?.let { avatar ->
                                                        DomainChannelPartial(
                                                            id = avatar.endpoint.innertubeCommand.browseEndpoint.browseId,
                                                            avatarUrl = avatar.image.sources.last().url
                                                        )
                                                    } ?: DomainChannelPartial(
                                                        id = videoData.channelId!!,
                                                        avatarUrl = videoData.decoratedAvatar!!.avatar.image.sources.last().url
                                                    ),
                                                    title = videoData.metadata.title,
                                                    subtitle = videoData.metadata.metadataDetails,
                                                    timestamp = videoData.thumbnail.timestampText
                                                )
                                            }
                                        }
                                    }

                                    is ApiSearch.HashtagTile -> {
                                        val (tagRenderer) = model.hashtagTileModel

                                        DomainTagPartial(
                                            name = tagRenderer.hashtag.elementsAttributedString.content,
                                            videosCount = tagRenderer.hashtagVideoCount.elementsAttributedString.content,
                                            channelsCount = tagRenderer.hashtagChannelCount.elementsAttributedString.content,
                                            backgroundColor = tagRenderer.hashtagBackgroundColor
                                        )
                                    }

                                    else -> null
                                }
                            }

                            else -> null
                        }
                    }
                },
            continuation = section.continuations.next
        )
    }

    suspend fun getChannel(id: String): DomainChannel {
        val channel = service.getChannel(id)
        val (channelHeaderModal) = channel.header.channelMobileHeaderRenderer.channelHeader.elementRenderer.model

        val channelProfile = channelHeaderModal.channelProfile
        val (avatars) = channelProfile.avatarData.avatar.image
        val banners = channelHeaderModal.channelBanner?.image?.sources

        val tabRenderer = channel.contents.content

        return DomainChannel(
            id = id,
            name = channelProfile.title,
            description = channelProfile.descriptionPreview.description,
            subscriberText = channelHeaderModal.channelProfile.metadata.subscriberCountText,
            avatar = avatars.first().url,
            banner = banners?.lastOrNull()?.url,
            items = tabRenderer.contents.mapNotNull { (shelfRenderer) ->
                shelfRenderer?.content?.horizontalListRenderer?.items
                    ?.firstOrNull()?.elementRenderer?.model?.gridVideoModel
                    ?.let { (videoData, onTap) ->
                        DomainVideoPartial(
                            id = onTap.innertubeCommand.watchEndpoint?.videoId.orEmpty(),
                            title = videoData.metadata.title,
                            subtitle = videoData.metadata.metadataDetails,
                            timestamp = videoData.thumbnail.timestampText
                        )
                    }
            },
            continuation = channel.continuations.next
        )
    }

    suspend fun getNext(videoId: String): DomainNext {
        val next = service.getNext(videoId)

        val (contents, continuations) = next.contents.singleColumnWatchNextResults.results.results

        val slimVideoMetadataSectionRenderer = contents
            .filterIsInstance<ApiNext.Renderer.SlimVideoMetadataSection>()
            .single()

        val (videoMetadata) = slimVideoMetadataSectionRenderer.contents[0].elementRenderer.model.videoMetadataModel!!
        val (videoActionBarButtons) = slimVideoMetadataSectionRenderer.contents[1].elementRenderer.model.videoActionBarModel!!
        val (channelBar) = slimVideoMetadataSectionRenderer.contents[2].elementRenderer.model.channelBarModel!!

        return DomainNext(
            viewCount = videoMetadata.subtitleData.viewCount.content,
            uploadDate = videoMetadata.subtitleData.date.content,
            channelAvatarUrl = channelBar.avatar.image.sources.first().url,
            likesText = videoActionBarButtons
                .filterIsInstance<ApiNext.Button.LikeButton>()
                .single().buttonData.defaultButton.title,
            subscribersText = channelBar.subtitle,
            comments = DomainNext.Comments(
                comments = emptyList(),
                continuation = null
            ),
            relatedVideos = DomainNext.RelatedVideos(
                videos = contents
                    .filterIsInstance<ApiNext.Renderer.ItemSection.RelatedItems>()
                    .map { it.contents }
                    .mapNotNull { (renderer) ->
                        renderer.elementRenderer.model.videoWithContextModel?.videoWithContextData?.toDomain()
                    },
                continuation = continuations.next
            ),
            badges = videoMetadata.badgesData.map { it.label }
        )
    }

    suspend fun getRelatedVideos(videoId: String, continuation: String): DomainNext.RelatedVideos {
        val response = service.getNext(videoId, continuation)
        val contents = response.continuationContents.contents

        return DomainNext.RelatedVideos(
            videos = contents.flatMap { (itemSectionRenderer) ->
                itemSectionRenderer.contents.mapNotNull { (elementRenderer) ->
                    elementRenderer.model.videoWithContextModel?.videoWithContextData?.toDomain()
                }
            },
            continuation = response.continuations.next
        )
    }

    suspend fun getVideo(id: String): DomainVideo {
        val player = service.getPlayer(id)
        val next = getNext(id)

        return DomainVideo(
            id = id,
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
                subscriptionsText = next.subscribersText
            ),
            badges = next.badges
        )
    }

    suspend fun getPlaylist(id: String): DomainPlaylist {
        val playlist = service.getPlaylist(id)
        val (headerRenderer) = playlist.header
        val (playlistVideoListRenderer) = playlist.contents.content.contents.single()

        return DomainPlaylist(
            id = id,
            name = headerRenderer.title.text,
            videoCount = headerRenderer.numVideosText.text,
            channel = DomainChannelPartial(
                id = headerRenderer.ownerEndpoint.browseEndpoint.browseId,
                name = headerRenderer.ownerText.text
            ),
            items = playlistVideoListRenderer.contents.mapNotNull { (renderer) ->
                if (renderer == null) return@mapNotNull null

                DomainVideoPartial(
                    id = renderer.videoId,
                    title = renderer.title.text,
                    subtitle = renderer.shortBylineText.text
                )
            },
            continuation = playlistVideoListRenderer.continuations.next
        )
    }

    suspend fun getPlaylist(id: String, continuation: String): DomainBrowse<DomainVideoPartial> {
        val (playlist) = service.getPlaylist(id, continuation).continuationContents

        return DomainBrowse(
            items = playlist.contents.mapNotNull { (renderer) ->
                renderer?.let {
                    DomainVideoPartial(
                        id = renderer.videoId,
                        title = renderer.title.text,
                        subtitle = renderer.shortBylineText.text
                    )
                }
            },
            continuation = playlist.continuations.next
        )
    }

    suspend fun getTag(tag: String): DomainTag {
        val response = service.getTag(tag)
        val (contents) = response.contents.browseResultsRenderer.content
        val (header) = contents.mapNotNull { it.itemSectionRenderer }.single().contents.single().elementRenderer.model.hashtagHeaderModel

        return DomainTag(
            name = response.header.translucentHeaderRenderer.title.text,
            subtitle = header.hashtagInfoText?.elementsAttributedString?.content.orEmpty(),
            avatars = header.avatarFacepile.map { it.elementsImage.sources.first().url },
            items = contents[1].shelfRenderer!!.content.horizontalListRenderer.items.map { (elementRenderer) ->
                val (onTap, videoData) = elementRenderer.model.videoWithContextModel.videoWithContextData
                val (reelEndpoint, watchEndpoint) = onTap.innertubeCommand

                DomainVideoPartial(
                    id = watchEndpoint?.videoId ?: reelEndpoint?.videoId!!,
                    title = videoData.metadata.title,
                    subtitle = "${videoData.metadata.byline} • ${videoData.metadata.metadataDetails}",
                    timestamp = videoData.thumbnail.timestampText
                )
            },
            continuation = response.continuations.next
        )
    }

    suspend fun getTagContinuation(continuation: String): DomainBrowse<DomainVideoPartial> {
        val response = service.getTagContinuation(continuation)
        val (contents) = response.continuationContents.sectionListContinuation

        return DomainBrowse(
            items = contents.single().shelfRenderer!!.content.horizontalListRenderer.items.map { (elementRenderer) ->
                val (onTap, videoData) = elementRenderer.model.videoWithContextModel.videoWithContextData
                val (reelEndpoint, watchEndpoint) = onTap.innertubeCommand

                DomainVideoPartial(
                    id = watchEndpoint?.videoId ?: reelEndpoint?.videoId!!,
                    title = videoData.metadata.title,
                    subtitle = "${videoData.metadata.byline} • ${videoData.metadata.metadataDetails}",
                    timestamp = videoData.thumbnail.timestampText
                )
            },
            continuation = response.continuations.next
        )
    }
}