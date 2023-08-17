package dev.zt64.innertube.domain.repository

import dev.zt64.innertube.domain.model.*
import dev.zt64.innertube.network.dto.ApiNext
import dev.zt64.innertube.network.dto.ApiTag
import dev.zt64.innertube.network.dto.ItemSection
import dev.zt64.innertube.network.dto.browse.*
import dev.zt64.innertube.network.dto.renderer.Renderer
import dev.zt64.innertube.network.service.InnerTubeService
import dev.zt64.innertube.network.service.RYDService
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class InnerTubeRepository(
    private val service: InnerTubeService,
    private val rydService: RYDService
) {
    suspend fun getTrendingVideos(continuation: String? = null): DomainBrowse<DomainVideoPartial> {
        val contents = if (continuation == null) {
            service.getTrending().contents.content
        } else {
            service.getTrending(continuation)
        }

        val items = contents.filterIsInstance<RichItemRenderer>()
        val continuationItem = contents.singleInstanceOrNull<ContinuationItem>()

        return DomainBrowse(
            items = emptyList(),
            continuation = continuationItem?.token
        )
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

    suspend fun getSearchSuggestions(query: String) =
        service.getSearchSuggestions(query).jsonArray[1].jsonArray
            .map { it.jsonArray[0].jsonPrimitive.content }

    suspend fun getSearchResults(
        query: String,
        continuation: String? = null
    ): DomainBrowse<Entity> {
        val contents = if (continuation != null) {
            service.getSearchResults(query, continuation).items
        } else {
            service.getSearchResults(query)
        }

        val itemSection = contents.filterIsInstance<ItemSection>().last()
        val continuationItem = contents.singleInstanceOrNull<ContinuationItem>()

        return DomainBrowse(
            items = itemSection.contents.mapNotNull(Renderer::toDomain),
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
        val relatedItemsContinuationItemRenderer: ApiNext.VideoSecondaryInfoRenderer =
            results.singleInstance()

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
            dislikesText = rydService.getVotes(id).dislikes.toString()/* .let { dislikes ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    CompactDecimalFormat
                        .getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
                        .format(dislikes)
                } else {
                    dislikes.toString()
                }
            } */,
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
        val continuation =
            videoListRenderer.lastOrNull() as? ApiPlaylist.SectionContent.ContinuationItem?

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