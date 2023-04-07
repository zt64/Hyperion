package com.zt.innertube.domain.repository

import android.icu.text.CompactDecimalFormat
import android.os.Build
import com.zt.innertube.domain.model.*
import com.zt.innertube.network.dto.ApiNext
import com.zt.innertube.network.dto.ApiSearch
import com.zt.innertube.network.dto.ApiTag
import com.zt.innertube.network.dto.browse.ApiPlaylist
import com.zt.innertube.network.dto.browse.ApiRecommended
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
        val contents = if (continuation == null) {
            service.getTrending().contents.content.contents
        } else {
            service.getTrending(continuation).onResponseReceivedActions.single().items
        }

        val items = contents.filterIsInstance<ApiRecommended.RichItemRenderer>()
        val continuationItem = contents.filterIsInstance<ApiRecommended.ContinuationItem>().singleOrNull()

        return DomainTrending(
            items = emptyList(),
            continuation = continuationItem?.token
        )
    }

    suspend fun getRecommendations(continuation: String? = null): DomainRecommended {
        val contents = if (continuation == null) {
            service.getRecommendations().contents.content.contents
        } else {
            service.getRecommendations(continuation).onResponseReceivedActions.single().items
        }

        val items = contents.filterIsInstance<ApiRecommended.RichItemRenderer>()
        val continuationItem = contents.filterIsInstance<ApiRecommended.ContinuationItem>().singleOrNull()

        return DomainRecommended(
            items = items.mapNotNull { renderer ->
                val v = renderer.content.videoRenderer ?: return@mapNotNull null

                DomainVideoPartial(
                    id = v.videoId,
                    title = v.title.text,
                    subtitle = listOfNotNull(
                        v.ownerText,
                        v.shortViewCountText,
                        v.publishedTimeText
                    ).joinToString(" • "),
                    timestamp = v.lengthText?.simpleText,
                    channel = v.channelThumbnailSupportedRenderers?.let {
                        DomainChannelPartial(
                            id = it.channelThumbnailWithLinkRenderer.navigationEndpoint.browseEndpoint.browseId,
                            avatarUrl = it.channelThumbnailWithLinkRenderer.thumbnail.sources.last().url
                        )
                    }
                )
            },
            continuation = continuationItem?.token
        )
    }

    suspend fun getSubscriptions() {
        service.getSubscriptions()
    }

    suspend fun getSearchSuggestions(query: String) = service.getSearchSuggestions(query).jsonArray[1].jsonArray
        .map { it.jsonArray[0].jsonPrimitive.content }

    suspend fun getSearchResults(query: String, continuation: String? = null): DomainSearch {
        val contents = if (continuation != null) {
            service.getSearchResults(query, continuation).onResponseReceivedCommands.single().items
        } else {
            service.getSearchResults(query).contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer.contents
        }

        val itemSection = contents.filterIsInstance<ApiSearch.ItemSection>().single()
        val continuationItem = contents.filterIsInstance<ApiSearch.ContinuationItem>().singleOrNull()

        return DomainSearch(
            items = itemSection.contents.mapNotNull { renderer ->
                when (renderer) {
                    is ApiSearch.ChannelRenderer -> {
                        DomainChannelPartial(
                            id = renderer.channelId,
                            name = renderer.title.simpleText,
                            avatarUrl = "https:${renderer.thumbnail.sources.last().url}",
                            subscriptionsText = renderer.subscriberCountText?.simpleText
                        )
                    }

                    is ApiSearch.VideoRenderer -> {
                        DomainVideoPartial(
                            id = renderer.videoId,
                            title = renderer.title.text,
                            subtitle = listOfNotNull(
                                renderer.longBylineText,
                                renderer.shortViewCountText,
                                renderer.publishedTimeText
                            ).joinToString(" • "),
                            timestamp = renderer.lengthText?.simpleText,
                            channel = DomainChannelPartial(
                                id = "",
                                avatarUrl = renderer.channelThumbnailSupportedRenderers?.channelThumbnailWithLinkRenderer?.thumbnail?.sources?.last()?.url
                            )
                        )
                    }

                    is ApiSearch.HashtagTileRenderer -> {
                        DomainTagPartial(
                            name = renderer.hashtag.simpleText,
                            channelsCount = renderer.hashtagChannelCount.simpleText,
                            videosCount = renderer.hashtagVideoCount.simpleText,
                            backgroundColor = renderer.hashtagBackgroundColor,
                        )
                    }

                    is ApiSearch.PlaylistRenderer -> {
                        DomainPlaylistPartial(
                            id = renderer.playlistId,
                            title = renderer.title.simpleText,
                            subtitle = renderer.shortBylineText.text,
                            videoCountText = renderer.videoCount,
                            thumbnailUrl = renderer.thumbnails.first().sources.last().url
                        )
                    }

                    ApiSearch.UnknownRenderer -> null
                }
            },
            continuation = continuationItem?.token
        )
    }

    suspend fun getChannel(id: String): DomainChannel {
        val channel = service.getChannel(id)
        val header = channel.header.c4TabbedHeaderRenderer

        return DomainChannel(
            id = id,
            name = header.title,
            description = "",
            subscriberText = header.subscriberCountText.simpleText,
            avatar = header.avatar.sources.last().url,
            banner = header.banner.sources.last(),
            items = emptyList(),
            continuation = null
        )
    }

    suspend fun getNext(videoId: String): DomainNext {
        val (contents, engagementPanels) = service.getNext(videoId)
        val (results, secondaryResults) = contents.twoColumnWatchNextResults

        val primaryInfoRenderer = results
            .filterIsInstance<ApiNext.VideoPrimaryInfoRenderer>()
            .single()

        val secondaryInfoRenderer = results
            .filterIsInstance<ApiNext.VideoSecondaryInfoRenderer>()
            .single()

        val relatedItemsContinuationItemRenderer = results
            .filterIsInstance<ApiNext.VideoSecondaryInfoRenderer>()
            .single()

        return DomainNext(
            viewCount = primaryInfoRenderer.viewCount.videoViewCountRenderer.shortViewCount.simpleText,
            uploadDate = primaryInfoRenderer.relativeDateText.simpleText,
            channelAvatarUrl = secondaryInfoRenderer.owner.videoOwnerRenderer.thumbnail.sources.first().url,
            likesText = primaryInfoRenderer.likesText,
            subscribersText = secondaryInfoRenderer.owner.videoOwnerRenderer.subscriberCountText.simpleText,
            comments = Comments(
                items = emptyList(),
                continuation = null
            ),
            relatedVideos = RelatedVideos(
                items = secondaryResults
                    .mapNotNull { it.compactVideoRenderer }
                    .map {
                        DomainVideoPartial(
                            id = it.videoId,
                            title = it.title.simpleText,
                            subtitle = listOfNotNull(
                                it.shortBylineText,
                                it.shortViewCountText,
                                it.publishedTimeText
                            ).joinToString(" • "),
                            timestamp = it.lengthText?.simpleText,
                            channel = DomainChannelPartial(
                                id = "",
                                avatarUrl = it.channelThumbnail.sources.last().url
                            )
                        )
                    },
                continuation = null
            ),
            badges = emptyList(),
            chapters = emptyList()
        )
    }

    suspend fun getRelatedVideos(videoId: String, continuation: String): RelatedVideos {
        val response = service.getNext(videoId, continuation)
        val contents = response.onResponseReceivedActions.single().items

        return RelatedVideos(
            items = emptyList(),
            continuation = null
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

    private fun List<ApiPlaylist.SectionContent.PlaylistVideo>.buildPlaylistItems(): List<DomainVideoPartial> = map {
        DomainVideoPartial(
            id = it.videoId,
            title = it.title.text,
            subtitle = "${it.shortBylineText} • ${it.videoInfo}",
            timestamp = it.lengthText.simpleText
        )
    }

    suspend fun getPlaylist(id: String): DomainPlaylist {
        val playlist = service.getPlaylist(id)
        val (headerRenderer) = playlist.header
        val (videoListRenderer) = playlist.contents.content.contents.single().itemSectionRenderer.contents.single()

        val videos = videoListRenderer.contents.filterIsInstance<ApiPlaylist.SectionContent.PlaylistVideo>()
        val continuation = videoListRenderer.contents.lastOrNull() as ApiPlaylist.SectionContent.ContinuationItem?

        return DomainPlaylist(
            id = id,
            name = headerRenderer.title.simpleText,
            videoCount = "",
            channel = DomainChannelPartial(
                id = headerRenderer.ownerEndpoint.browseEndpoint.browseId,
                name = headerRenderer.ownerText.text
            ),
            items = videos.buildPlaylistItems(),
            continuation = continuation?.token
        )
    }

    suspend fun getPlaylist(id: String, continuation: String): DomainBrowse<DomainVideoPartial> {
        val (items) = service.getPlaylist(id, continuation).onResponseReceivedActions.single()

        val videos = items.filterIsInstance<ApiPlaylist.SectionContent.PlaylistVideo>()
        val continuationItem = items.lastOrNull() as ApiPlaylist.SectionContent.ContinuationItem?

        return DomainBrowse(
            items = videos.buildPlaylistItems(),
            continuation = continuationItem?.token
        )
    }

    suspend fun getTag(tag: String): DomainTag {
        val (header, contents) = service.getTag(tag)
        val items = contents.content.contents.filterIsInstance<ApiTag.RichItemRenderer>()

        return DomainTag(
            name = header.hashtagHeaderRenderer.hashtag.simpleText,
            subtitle = header.hashtagHeaderRenderer.hashtagInfoText.simpleText,
            items = items.mapNotNull { renderer ->
                val v = renderer.content.videoRenderer ?: return@mapNotNull null

                DomainVideoPartial(
                    id = v.videoId,
                    title = v.title.text,
                    subtitle = listOfNotNull(
                        v.ownerText,
                        v.shortViewCountText,
                        v.publishedTimeText
                    ).joinToString(" • "),
                    timestamp = v.lengthText?.simpleText,
                    channel = v.channelThumbnailSupportedRenderers?.let {
                        DomainChannelPartial(
                            id = it.channelThumbnailWithLinkRenderer.navigationEndpoint.browseEndpoint.browseId,
                            avatarUrl = it.channelThumbnailWithLinkRenderer.thumbnail.sources.last().url
                        )
                    }
                )
            },
            continuation = null
        )
    }

    suspend fun getTagContinuation(continuation: String): DomainBrowse<DomainVideoPartial> {
        val response = service.getTagContinuation(continuation)
        val contents = response.onResponseReceivedActions.single().items

        val items = contents.filterIsInstance<ApiTag.RichItemRenderer>()

        return DomainBrowse(
            items = items.mapNotNull { renderer ->
                val v = renderer.content.videoRenderer ?: return@mapNotNull null

                DomainVideoPartial(
                    id = v.videoId,
                    title = v.title.text,
                    subtitle = listOfNotNull(
                        v.ownerText,
                        v.shortViewCountText,
                        v.publishedTimeText
                    ).joinToString(" • "),
                    timestamp = v.lengthText?.simpleText,
                    channel = v.channelThumbnailSupportedRenderers?.let {
                        DomainChannelPartial(
                            id = it.channelThumbnailWithLinkRenderer.navigationEndpoint.browseEndpoint.browseId,
                            avatarUrl = it.channelThumbnailWithLinkRenderer.thumbnail.sources.last().url
                        )
                    }
                )
            },
            continuation = null
        )
    }
}