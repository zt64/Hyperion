package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
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
            data class Tab(val tabRenderer: TabRenderer) {
                @Serializable
                data class TabRenderer(val content: TabContent? = null) {
                    @Serializable
                    data class TabContent(val sectionListRenderer: SectionList)
                }
            }
        }
    }

    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionList)

    @Serializable
    data class SectionList(
        val contents: List<SectionContent>? = null,
        val continuations: List<ApiContinuation>
    ) {
        @Serializable
        data class SectionContent(val itemSectionRenderer: ItemSectionRenderer? = null) {
            @Serializable
            data class ItemSectionRenderer(val contents: List<Content>) {
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
                val avatarImageSize: String,
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
                val maxLinesMetadataDetails: Int,
                val maxTitleLine: Int,
                val metadataDetails: String,
                val title: String
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