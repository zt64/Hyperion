package com.zt.innertube.domain.repository

import android.icu.text.CompactDecimalFormat
import android.os.Build
import com.zt.innertube.domain.model.*
import com.zt.innertube.network.dto.ApiNext
import com.zt.innertube.network.dto.ApiSearch
import com.zt.innertube.network.dto.ApiTag
import com.zt.innertube.network.dto.browse.ApiPlaylist
import com.zt.innertube.network.dto.browse.ApiRecommended
import com.zt.innertube.network.dto.browse.ChannelTab
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
            service.getTrending().contents.content
        } else {
            service.getTrending(continuation)
        }

        val items = contents.filterIsInstance<ApiRecommended.RichItemRenderer>()
        val continuationItem = contents.singleInstanceOrNull<ApiRecommended.ContinuationItem>()

        return DomainTrending(
            items = emptyList(),
            continuation = continuationItem?.token
        )
    }

    suspend fun getRecommendations(continuation: String? = null): DomainRecommended {
        val contents = if (continuation == null) {
            service.getRecommendations().contents.content
        } else {
            service.getRecommendations(continuation)
        }

        val items = contents.filterIsInstance<ApiRecommended.RichItemRenderer>()
        val continuationItem = contents.singleInstanceOrNull<ApiRecommended.ContinuationItem>()

        return DomainRecommended(
            items = items.mapNotNull { renderer ->
                val v = renderer.content.videoRenderer ?: return@mapNotNull null

                DomainVideoPartial(
                    id = v.videoId,
                    title = v.title,
                    viewCount = v.shortViewCountText,
                    publishedTimeText = v.publishedTimeText,
                    ownerText = v.ownerText,
                    timestamp = v.lengthText,
                    channel = v.channelThumbnailSupportedRenderers?.toDomain()
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
            service.getSearchResults(query).contents
        }

        val itemSection = contents.filterIsInstance<ApiSearch.ItemSection>().last()
        val continuationItem = contents.singleInstanceOrNull<ApiSearch.ContinuationItem>()

        return DomainSearch(
            items = itemSection.contents.mapNotNull { renderer ->
                when (renderer) {
                    is ApiSearch.ChannelRenderer -> {
                        DomainChannelPartial(
                            id = renderer.channelId,
                            name = renderer.title,
                            avatarUrl = "https:${renderer.thumbnail.sources.last().url}",
                            subscriptionsText = renderer.subscriberCountText
                        )
                    }

                    is ApiSearch.VideoRenderer -> {
                        DomainVideoPartial(
                            id = renderer.videoId,
                            title = renderer.title,
                            viewCount = renderer.shortViewCountText,
                            publishedTimeText = renderer.publishedTimeText,
                            ownerText = renderer.ownerText,
                            timestamp = renderer.lengthText,
                            channel = renderer.channelThumbnailSupportedRenderers?.toDomain()
                        )
                    }

                    is ApiSearch.HashtagTileRenderer -> {
                        DomainTagPartial(
                            name = renderer.hashtag,
                            channelsCount = renderer.hashtagChannelCount,
                            videosCount = renderer.hashtagVideoCount,
                            backgroundColor = renderer.hashtagBackgroundColor,
                        )
                    }

                    is ApiSearch.PlaylistRenderer -> {
                        DomainPlaylistPartial(
                            id = renderer.playlistId,
                            title = renderer.title,
                            subtitle = renderer.shortBylineText,
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
        //        val tabs = channel.contents.tabs.map { it.type }

        return DomainChannel(
            id = id,
            name = header.title,
            description = "",
            subscriberText = header.subscriberCountText,
            avatar = header.avatar.sources.last().url,
            banner = header.banner?.sources?.last(),
            tabs = emptyList()
        )
    }

    suspend fun getChannel(id: String, tab: ChannelTab) {
        val channel = service.getChannel(id, tab)
        val header = channel.header.c4TabbedHeaderRenderer
    }

    suspend fun getNext(videoId: String): DomainNext {
        val (contents, engagementPanels) = service.getNext(videoId)
        val (results, secondaryResults) = contents.twoColumnWatchNextResults

        val primaryInfoRenderer: ApiNext.VideoPrimaryInfoRenderer = results.singleInstance()
        val secondaryInfoRenderer: ApiNext.VideoSecondaryInfoRenderer = results.singleInstance()
        val relatedItemsContinuationItemRenderer: ApiNext.VideoSecondaryInfoRenderer = results.singleInstance()

        return DomainNext(
            viewCount = primaryInfoRenderer.viewCount.videoViewCountRenderer.shortViewCount,
            uploadDate = primaryInfoRenderer.relativeDateText,
            channelAvatarUrl = secondaryInfoRenderer.owner.videoOwnerRenderer.thumbnail.sources.first().url,
            likesText = primaryInfoRenderer.likesText,
            subscribersText = secondaryInfoRenderer.owner.videoOwnerRenderer.subscriberCountText,
            comments = Comments(
                items = emptyList(),
                continuation = null
            ),
            relatedVideos = RelatedVideos(
                items = secondaryResults.mapNotNull { (renderer) ->
                    if (renderer == null) return@mapNotNull null

                    DomainVideoPartial(
                        id = renderer.videoId,
                        title = renderer.title,
                        timestamp = renderer.lengthText,
                        viewCount = renderer.shortViewCountText,
                        publishedTimeText = renderer.publishedTimeText,
                        ownerText = renderer.shortBylineText,
                        channel = DomainChannelPartial(
                            id = "",
                            avatarUrl = renderer.channelThumbnail.sources.last().url
                        )
                    )
                },
                continuation = null
            ),
            badges = emptyList(),
            chapters = engagementPanels.singleInstanceOrNull<ApiNext.Chapters>()?.chapters?.map {
                DomainChapter(
                    title = it.title,
                    start = it.start,
                    thumbnail = it.thumbnail.sources.last().url
                )
            }.orEmpty()
        )
    }

    suspend fun getRelatedVideos(videoId: String, continuation: String): RelatedVideos {
        val response = service.getNext(videoId, continuation)

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

    private fun List<ApiPlaylist.SectionContent.PlaylistVideo>.buildPlaylistItems() = map {
        DomainVideoPartial(
            id = it.videoId,
            title = it.title,
            viewCount = it.videoInfo,
            publishedTimeText = it.shortBylineText,
            timestamp = it.lengthText
        )
    }

    suspend fun getPlaylist(id: String): DomainPlaylist {
        val playlist = service.getPlaylist(id)
        val (headerRenderer) = playlist.header
        val (videoListRenderer) = playlist.contents.content.single()

        val videos = videoListRenderer.filterIsInstance<ApiPlaylist.SectionContent.PlaylistVideo>()
        val continuation = videoListRenderer.lastOrNull() as? ApiPlaylist.SectionContent.ContinuationItem?

        return DomainPlaylist(
            id = id,
            name = headerRenderer.title,
            videoCount = "",
            channel = DomainChannelPartial(
                id = headerRenderer.ownerEndpoint.browseEndpoint.browseId,
                name = headerRenderer.ownerText
            ),
            items = videos.buildPlaylistItems(),
            continuation = continuation?.token
        )
    }

    suspend fun getPlaylist(id: String, continuation: String): DomainBrowse<DomainVideoPartial> {
        val items = service.getPlaylist(id, continuation)

        val videos = items.filterIsInstance<ApiPlaylist.SectionContent.PlaylistVideo>()
        val continuationItem = items.lastOrNull() as ApiPlaylist.SectionContent.ContinuationItem?

        return DomainBrowse(
            items = videos.buildPlaylistItems(),
            continuation = continuationItem?.token
        )
    }

    suspend fun getTag(tag: String): DomainTag {
        val (header, contents) = service.getTag(tag)

        return DomainTag(
            name = header.hashtagHeaderRenderer.hashtag,
            subtitle = header.hashtagHeaderRenderer.hashtagInfoText,
            items = contents
                .content
                .filterIsInstance<ApiTag.RichItemRenderer>()
                .mapNotNull { renderer ->
                    val v = renderer.content.videoRenderer ?: return@mapNotNull null

                    DomainVideoPartial(
                        id = v.videoId,
                        title = v.title,
                        viewCount = v.shortViewCountText,
                        publishedTimeText = v.publishedTimeText,
                        ownerText = v.ownerText,
                        timestamp = v.lengthText,
                        channel = v.channelThumbnailSupportedRenderers?.toDomain()
                    )
                },
            continuation = null
        )
    }

    suspend fun getTagContinuation(continuation: String): DomainBrowse<DomainVideoPartial> {
        val contents = service.getTagContinuation(continuation)

        return DomainBrowse(
            items = contents
                .filterIsInstance<ApiTag.RichItemRenderer>()
                .mapNotNull { renderer ->
                    val v = renderer.content.videoRenderer ?: return@mapNotNull null

                    DomainVideoPartial(
                        id = v.videoId,
                        title = v.title,
                        viewCount = v.shortViewCountText,
                        publishedTimeText = v.publishedTimeText,
                        timestamp = v.lengthText,
                        channel = v.channelThumbnailSupportedRenderers?.let {
                            DomainChannelPartial(
                                id = it.channelThumbnailWithLinkRenderer.navigationEndpoint.browseEndpoint.browseId,
                                name = v.ownerText,
                                avatarUrl = it.channelThumbnailWithLinkRenderer.thumbnail.sources.last().url
                            )
                        }
                    )
                },
            continuation = null
        )
    }

    private companion object {
        private inline fun <reified T> Iterable<*>.singleInstance(): T = first { it is T } as T
        private inline fun <reified T> Iterable<*>.singleInstanceOrNull(): T? = firstOrNull { it is T } as T?
    }
}