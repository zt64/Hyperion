package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ItemSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
data class ApiTrending(
    val contents: Contents,
    val continuationContents: ContinuationContents? = null
) {
    @Serializable
    data class Contents(val singleColumnBrowseResultsRenderer: BrowseResultsRenderer) {
        @Serializable
        data class BrowseResultsRenderer(val tabs: List<Tab>) {
            @Serializable
            data class Tab(val tabRenderer: TabRenderer)

            @Serializable
            data class TabRenderer(val content: TabContent? = null)

            @Serializable
            data class TabContent(val sectionListRenderer: SectionList)
        }
    }

    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionList)

    @Serializable
    data class SectionList(
        val contents: List<SectionContent> = emptyList(),
        val continuations: List<ApiContinuation> = emptyList()
    ) {
        @Serializable
        data class SectionContent(val itemSectionRenderer: ItemSectionRenderer<Content>? = null) {
            @Serializable
            data class Content(val elementRenderer: ElementRenderer<Model>? = null) {
                @Serializable
                data class Model(val videoWithContextModel: VideoWithContextModel? = null) {
                    @Serializable
                    data class VideoWithContextModel(val videoWithContextData: VideoWithContextData)
                }
            }
        }
    }

    @Serializable
    data class VideoWithContextData(
        val videoData: VideoData,
        val onTap: OnTap
    ) {
        @Serializable
        data class VideoData(
            val avatar: Avatar,
            val metadata: Metadata,
            val thumbnail: Thumbnail
        ) {
            @Serializable
            data class Avatar(
                val endpoint: Endpoint,
                val image: ApiImage
            ) {
                @Serializable
                data class Endpoint(val innertubeCommand: InnertubeCommand) {
                    @Serializable
                    data class InnertubeCommand(val browseEndpoint: ApiBrowseEndpoint)
                }
            }

            @Serializable
            data class Metadata(
                val title: String,
                val metadataDetails: String
            )

            @Serializable
            data class Thumbnail(
                val image: ApiImage,
                val timestampText: String,
            )
        }

        @Serializable
        data class OnTap(val innertubeCommand: InnertubeCommand) {
            @Serializable
            data class InnertubeCommand(val watchEndpoint: WatchEndpoint)
        }
    }
}