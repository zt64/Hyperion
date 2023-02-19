package com.zt.innertube.domain.repository

import android.icu.text.CompactDecimalFormat
import android.os.Build
import com.zt.innertube.domain.mapper.toDomain
import com.zt.innertube.domain.model.*
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
                renderer.contents.single().elementRenderer.model.videoWithContextModel?.toDomain()
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
            items = section.contents.flatMap { (renderer) ->
                renderer.contents.mapNotNull {
                    it.elementRenderer?.model?.videoWithContextModel?.toDomain()
                }
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
                                    thumbnailUrl = "https:${renderer.compactChannelRenderer.thumbnail.sources.last().url}",
                                    subscriptionsText = renderer.compactChannelRenderer.subscriberCountText?.text,
                                    videoCountText = renderer.compactChannelRenderer.videoCountText?.text
                                )
                            }

                            is ApiSearch.Renderer.Element -> {
                                when (val model = renderer.elementRenderer.model) {
                                    is ApiSearch.VideoWithContextSlots -> {
                                        val (onTap, videoData) = model.videoWithContextData

                                        when (videoData) {
                                            is ApiSearch.VideoWithContextSlots.Mix -> {
                                                DomainMixPartial(
                                                    id = onTap.innertubeCommand.watchEndpoint!!.videoId!!,
                                                    title = videoData.metadata.title,
                                                    subtitle = videoData.metadata.byline,
                                                    thumbnailUrl = videoData.thumbnail.sources.last().url
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
                                                        avatarUrl = videoData.decoratedAvatar!!.sources.last().url
                                                    ),
                                                    title = videoData.metadata.title,
                                                    subtitle = videoData.metadata.metadataDetails,
                                                    timestamp = videoData.thumbnail.timestampText
                                                )
                                            }
                                        }
                                    }

                                    is ApiSearch.HashtagTile -> {
                                        val (tagRenderer) = model

                                        DomainTagPartial(
                                            name = tagRenderer.hashtag.elementsAttributedString.content,
                                            videosCount = tagRenderer.hashtagVideoCount?.elementsAttributedString?.content,
                                            channelsCount = tagRenderer.hashtagChannelCount?.elementsAttributedString?.content,
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
        val (avatars) = channelProfile.avatarData.avatar
        val banners = channelHeaderModal.channelBanner?.sources

        val tabRenderer = channel.contents.content

        return DomainChannel(
            id = id,
            name = channelProfile.title,
            description = channelProfile.descriptionPreview.description,
            subscriberText = channelHeaderModal.channelProfile.metadata.subscriberCountText,
            avatar = avatars.first().url,
            banner = banners?.lastOrNull(),
            items = tabRenderer.contents.mapNotNull { (shelfRenderer) ->
                shelfRenderer?.content?.horizontalListRenderer?.items?.firstOrNull()
                    ?.elementRenderer?.model?.gridVideoModel?.toDomain()
            },
            continuation = channel.continuations.next
        )
    }

    suspend fun getNext(videoId: String): DomainNext {
        val next = service.getNext(videoId)

        val (contents, continuations) = next.contents.singleColumnWatchNextResults.results.results

        val metadataSectionRenderer = contents
            .filterIsInstance<ApiNext.Renderer.SlimVideoMetadataSection>()
            .single()

        val models = metadataSectionRenderer.contents.map { it.elementRenderer.model }
        val (videoMetadata) = models.find { it.videoMetadataModel != null }!!.videoMetadataModel!!
        val (videoActionBarButtons) = models.find { it.videoActionBarModel != null }!!.videoActionBarModel!!
        val (channelBar) = models.find { it.channelBarModel != null }!!.channelBarModel!!

        return DomainNext(
            viewCount = videoMetadata.subtitleData.viewCount.content,
            uploadDate = videoMetadata.subtitleData.date.content,
            channelAvatarUrl = channelBar.avatar.sources.first().url,
            likesText = videoActionBarButtons
                .filterIsInstance<ApiNext.Button.LikeButton>()
                .single().buttonData.defaultButton.title,
            subscribersText = channelBar.subtitle,
            comments = Comments(
                items = emptyList(),
                continuation = null
            ),
            relatedVideos = RelatedVideos(
                items = contents
                    .filterIsInstance<ApiNext.Renderer.RelatedItems>()
                    .map { it.contents }
                    .mapNotNull { (renderer) ->
                        renderer.elementRenderer.model.videoWithContextModel?.toDomain()
                    },
                continuation = continuations.next
            ),
            badges = videoMetadata.badgesData.map { it.label },
            chapters = next.engagementPanels
                .filterIsInstance<ApiNext.Chapters>()
                .singleOrNull()
                ?.content?.sectionListRenderer?.contents?.single()?.itemSectionRenderer?.contents?.mapNotNull { (renderer) ->
                    val chapter = renderer.model.macroMarkersListItemModel?.renderer ?: return@mapNotNull null

                    DomainChapter(
                        title = chapter.title.elementsAttributedString.content,
                        thumbnail = chapter.thumbnail.elementsImage.sources.last().url,
                        start = chapter.onTap.watchEndpoint.startTime
                    )
                } ?: emptyList()
        )
    }

    suspend fun getRelatedVideos(videoId: String, continuation: String): RelatedVideos {
        val response = service.getNext(videoId, continuation)
        val contents = response.continuationContents.contents

        return RelatedVideos(
            items = contents.flatMap { (itemSectionRenderer) ->
                itemSectionRenderer.contents.mapNotNull { (elementRenderer) ->
                    elementRenderer.model.videoWithContextModel?.toDomain()
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
            formats = player.streamingData.adaptiveFormats,
            author = DomainChannelPartial(
                id = player.videoDetails.channelId,
                name = player.videoDetails.author,
                avatarUrl = next.channelAvatarUrl,
                subscriptionsText = next.subscribersText
            ),
            badges = next.badges,
            chapters = next.chapters
        )
    }

    suspend fun getComments(id: String, page: Int) {
        val comments = service.getComments(id, page)

        //        comments.continuationContents.sectionListContinuation.contents.map {  }
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
            items = playlist.contents
                .mapNotNull { it.playlistVideoRenderer }
                .map { renderer ->
                    DomainVideoPartial(
                        id = renderer.videoId,
                        title = renderer.title.text,
                        subtitle = renderer.shortBylineText.text
                    )
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