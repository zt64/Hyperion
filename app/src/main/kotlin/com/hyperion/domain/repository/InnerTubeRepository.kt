package com.hyperion.domain.repository

import com.hyperion.domain.mapper.toDomain
import com.hyperion.domain.model.DomainChannelPartial
import com.hyperion.domain.model.DomainTrending
import com.hyperion.domain.model.DomainVideo
import com.hyperion.network.dto.ApiFormat
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
                it.itemSectionRenderer?.contents?.single()?.videoWithContextRenderer?.toDomain()
            }
        )
    }

    suspend fun getSuggestions(query: String) = service.getSuggestions(query).jsonArray[1].jsonArray
        .map { it.jsonArray[0].jsonPrimitive.content }

    suspend fun search(query: String) {
        service.search(query).contents
    }

    suspend fun getChannel(id: String) = service.getChannel(id).toDomain()

    suspend fun getVideo(id: String): DomainVideo {
        val player = service.getPlayer(id)
        val next = service.getNext(id)

        return DomainVideo(
            id = player.videoDetails.videoId,
            title = player.videoDetails.title,
            subtitle = next.contents.singleColumnWatchNextResults.results.results.contents[0].slimVideoMetadataSectionRenderer!!.contents[0]
                .elementRenderer.newElement.type.componentType.model.videoMetadataModel!!.videoMetadata.subtitleData.viewCount.content,
            description = player.videoDetails.shortDescription,
            views = player.videoDetails.viewCount.toInt(),
            likesText = next.contents.singleColumnWatchNextResults.results.results.contents[0].slimVideoMetadataSectionRenderer!!.contents[1]
                .elementRenderer.newElement.type.componentType.model.videoActionBarModel!!.buttons[0].likeButton!!.buttonData.defaultButton.title,
            // TODO: Fetch from RYD API
            dislikes = rydService.getVotes(id).dislikes,
            streams = player.streamingData.formats.map(ApiFormat::toDomain),
            author = DomainChannelPartial(
                id = player.videoDetails.channelId,
                name = player.videoDetails.author,
                avatarUrl = next.contents.singleColumnWatchNextResults.results.results.contents[0].slimVideoMetadataSectionRenderer!!.contents[2]
                    .elementRenderer.newElement.type.componentType.model.channelBarModel!!.videoChannelBarData.avatar.image.sources[0].url,
                subscriberText = next.contents.singleColumnWatchNextResults.results.results.contents[0].slimVideoMetadataSectionRenderer!!.contents[2]
                    .elementRenderer.newElement.type.componentType.model.channelBarModel!!.videoChannelBarData.subtitle
            )
        )
    }
}