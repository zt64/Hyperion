package dev.zt64.innertube.domain.repository

import dev.zt64.innertube.domain.model.Comments
import dev.zt64.innertube.domain.model.DomainBrowse
import dev.zt64.innertube.domain.model.DomainChannel
import dev.zt64.innertube.domain.model.DomainChannelPartial
import dev.zt64.innertube.domain.model.DomainChapter
import dev.zt64.innertube.domain.model.DomainNext
import dev.zt64.innertube.domain.model.DomainPlayer
import dev.zt64.innertube.domain.model.DomainTag
import dev.zt64.innertube.domain.model.DomainVideo
import dev.zt64.innertube.domain.model.DomainVideoPartial
import dev.zt64.innertube.domain.model.RelatedVideos
import dev.zt64.innertube.model.ListResponse
import dev.zt64.innertube.model.Playlist
import dev.zt64.innertube.model.PlaylistItem
import dev.zt64.innertube.model.SearchResult
import dev.zt64.innertube.model.Thumbnails
import dev.zt64.innertube.network.dto.ApiNext
import dev.zt64.innertube.network.dto.ApiTag
import dev.zt64.innertube.network.dto.browse.ApiPlaylist
import dev.zt64.innertube.network.dto.browse.ChannelTab
import dev.zt64.innertube.network.dto.browse.ContinuationItem
import dev.zt64.innertube.network.dto.browse.RichItemRenderer
import dev.zt64.innertube.network.service.InnerTubeService
import dev.zt64.innertube.network.service.RYDService
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class InnerTubeRepository(
    private val service: InnerTubeService,
    private val rydService: RYDService
) {
    suspend fun getTrendingVideos(nextPageToken: String? = null): DomainBrowse<DomainVideoPartial> {
        service.getVideos("trending", nextPageToken)

        TODO()
    }

    suspend fun getRecommendations(continuation: String? = null): DomainBrowse<DomainVideoPartial> {
        val contents = if (continuation == null) {
            service.getRecommendations().contents.content
        } else {
            service.getRecommendations(continuation)
        }

        val items = contents.filterIsInstance<RichItemRenderer>()
        val continuationItem = contents.singleInstanceOrNull<ContinuationItem>()

        return DomainBrowse(
            items = items.mapNotNull { renderer ->
                renderer.content.videoRenderer?.toDomain()
            },
            continuation = continuationItem?.token
        )
    }

    suspend fun getSubscriptions() {
        service.getSubscriptions()
    }

    suspend fun getSearchSuggestions(query: String) = service
        .getSearchSuggestions(query)
        .jsonArray[1]
        .jsonArray
        .map { it.jsonArray[0].jsonPrimitive.content }

    // suspend fun getSearchResults(
    //     query: String,
    //     continuation: String? = null
    // ): DomainBrowse<Entity> {
    //     val contents = if (continuation != null) {
    //         service.getSearchResults(query, continuation).items
    //     } else {
    //         service.getSearchResults(query)
    //     }
    //
    //     val itemSection = contents.filterIsInstance<ItemSection>().last()
    //     val continuationItem = contents.singleInstanceOrNull<ContinuationItem>()
    //
    //     return DomainBrowse(
    //         items = itemSection.contents.mapNotNull(Renderer::toDomain),
    //         continuation = continuationItem?.token
    //     )
    // }

    suspend fun getSearchResults(
        query: String,
        pageToken: String? = null
    ): ListResponse<SearchResult> {
        val res = service.getSearchResults(query, pageToken)

        return ListResponse(
            results = res.items.map { SearchResult.fromYt(it) },
            nextToken = res.nextPageToken,
            prevToken = res.prevPageToken
        )
    }

    suspend fun getVideos(ids: List<String>) {
        val res = service.getVideos(ids)
    }

    suspend fun getChannels(ids: List<String>) {
        val res = service.getChannels(ids)
    }

    suspend fun getPlaylists(ids: List<String>) {
        val res = service.getPlaylists(ids)
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
            avatar = header
                .avatar
                .sources
                .last()
                .url,
            banner = header.banner?.sources?.last(),
            tabs = emptyList()
        )
    }

    suspend fun getChannel(
        id: String,
        tab: ChannelTab
    ) {

    }

    suspend fun getNext(videoId: String): DomainNext {
        val (contents, engagementPanels) = service.getNext(videoId)
        val (results, secondaryResults) = contents.twoColumnWatchNextResults

        val primaryInfoRenderer: ApiNext.VideoPrimaryInfoRenderer = results.singleInstance()
        val secondaryInfoRenderer: ApiNext.VideoSecondaryInfoRenderer = results.singleInstance()
        val relatedItemsContinuationItemRenderer: ApiNext.VideoSecondaryInfoRenderer =
            results.singleInstance()

        return DomainNext(
            viewCount = primaryInfoRenderer.viewCount.videoViewCountRenderer.shortViewCount
                ?: "Whar",
            uploadDate = primaryInfoRenderer.relativeDateText,
            channelAvatarUrl = secondaryInfoRenderer
                .owner
                .videoOwnerRenderer
                .thumbnail
                .sources
                .first()
                .url,
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
                        channel =
                        DomainChannelPartial(
                            id = "",
                            avatarUrl =
                            renderer
                                .channelThumbnail
                                .sources
                                .last()
                                .url
                        )
                    )
                },
                continuation = null
            ),
            badges = emptyList(),
            chapters = engagementPanels
                .singleInstanceOrNull<ApiNext.Chapters>()
                ?.chapters
                ?.map {
                    DomainChapter(
                        title = it.title,
                        start = it.start,
                        thumbnail = it
                            .thumbnail
                            .sources
                            .last()
                            .url
                    )
                }.orEmpty()
        )
    }

    suspend fun getRelatedVideos(
        videoId: String,
        continuation: String
    ): RelatedVideos {
        val response = service.getNext(videoId, continuation)

        return RelatedVideos(
            items = emptyList(),
            continuation = null
        )
    }

    /**
     * Get video player
     *
     * @param id Video ID
     */
    suspend fun getPlayer(id: String): DomainPlayer {
        val player = service.getPlayer(id)

        return DomainPlayer(
            formats = player.streamingData.adaptiveFormats
        )
    }

    suspend fun getVideo(id: String): DomainVideo {
        val player = getPlayer(id)
        // val next = getNext(id)
        val video = service.getVideo(id)

        return DomainVideo(
            id = id,
            title = video.snippet.title!!,
            viewCount = video.statistics.viewCount.toString(),
            uploadDate = video.snippet.publishedAt.toString(),
            description = video.snippet.description,
            likesText = video.statistics.likeCount!!.toString(),
            dislikesText = rydService.getVotes(id).dislikes.toString(),
            formats = player.formats,
            author = DomainChannelPartial(
                id = video.snippet.channelId,
                name = video.snippet.channelTitle,
                avatarUrl = video.snippet.channelId,
                subscriptionsText = video.snippet.channelTitle
            ),
            badges = video.snippet.tags,
            chapters = emptyList()
        )
    }

    suspend fun getComments(
        id: String,
        page: String
    ) {
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

    suspend fun getPlaylist(id: String): Playlist {
        val playlist = service.getPlaylist(id)

        return Playlist(
            id = id,
            title = playlist.snippet.title,
            description = playlist.snippet.description,
            channelTitle = playlist.snippet.channelTitle,
            thumbnails = Thumbnails(playlist.snippet.thumbnails),
            publishedAt = playlist.snippet.publishedAt,
            itemCount = playlist.contentDetails.itemCount
        )
    }

    suspend fun getPlaylistItems(
        id: String,
        token: String? = null
    ): ListResponse<PlaylistItem> {
        val res = service.getPlaylistItems(id, token)

        return ListResponse(
            results = res.items.map { PlaylistItem(it) },
            nextToken = res.nextPageToken,
            prevToken = res.prevPageToken
        )
    }

    // suspend fun getPlaylist(id: String, continuation: String): DomainBrowse<DomainVideoPartial> {
    //     val items = service.getPlaylist(id, continuation)
    //
    //     val videos = items.filterIsInstance<ApiPlaylist.SectionContent.PlaylistVideo>()
    //     val continuationItem = items.lastOrNull() as ApiPlaylist.SectionContent.ContinuationItem?
    //
    //     return DomainBrowse(
    //         items = videos.buildPlaylistItems(),
    //         continuation = continuationItem?.token
    //     )
    // }

    suspend fun getTag(tag: String): DomainTag {
        val (header, contents) = service.getTag(tag)

        return DomainTag(
            name = header.hashtagHeaderRenderer.hashtag,
            subtitle = header.hashtagHeaderRenderer.hashtagInfoText,
            items = contents
                .content
                .filterIsInstance<ApiTag.RichItemRenderer>()
                .mapNotNull { renderer ->
                    renderer.content.videoRenderer?.toDomain()
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
                    renderer.content.videoRenderer?.toDomain()
                },
            continuation = null
        )
    }

    private companion object {
        inline fun <reified T> Iterable<*>.singleInstance() = first { it is T } as T

        inline fun <reified T> Iterable<*>.singleInstanceOrNull() = firstOrNull { it is T } as T?
    }
}

abstract class Continuable<T> : Iterator<List<T>> {
    private var continuation: String? = null

    override fun hasNext(): Boolean = continuation != null

    override fun next(): List<T> {
        val items = getItems()
        getContinuation(items)
        return items
    }

    protected abstract fun getItems(): List<T>

    protected abstract fun getContinuation(items: List<T>): String?
}