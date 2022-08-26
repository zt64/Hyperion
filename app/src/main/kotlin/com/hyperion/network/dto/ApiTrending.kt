package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ItemSectionRenderer
import com.hyperion.network.dto.renderer.SectionListRenderer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiTrending(val contents: Contents) {
    @Serializable
    data class Contents(
        @SerialName("singleColumnBrowseResultsRenderer")
        val browseResultsRenderer: BrowseResultsRenderer
    ) {
        @Serializable
        data class BrowseResultsRenderer(val tabs: List<Tab>) {
            @Serializable
            data class Tab(val tabRenderer: TabRenderer)

            @Serializable
            data class TabRenderer(val content: TabContent? = null)

            @Serializable
            data class TabContent(val sectionListRenderer: SectionListRenderer<SectionContent>)
        }
    }

    @Serializable
    data class SectionContent(val itemSectionRenderer: ItemSectionRenderer<Content>? = null) {
        @Serializable
        data class Content(val elementRenderer: ElementRenderer<Model>? = null) {
            @Serializable
            data class Model(val videoWithContextModel: VideoWithContextModel? = null)
        }
    }

    @Serializable
    data class VideoWithContextModel(val videoWithContextData: VideoWithContextData) {
        @Serializable
        data class VideoWithContextData(
            val videoData: VideoData,
            val onTap: OnTap
        ) {
            @Serializable
            data class VideoData(
                val avatar: ApiAvatar,
                val metadata: Metadata,
                val thumbnail: ApiThumbnailTimestamp
            ) {
                @Serializable
                data class Metadata(
                    val title: String,
                    val metadataDetails: String
                )
            }

            @Serializable
            data class OnTap(val innertubeCommand: ApiWatchCommand) {
                @Serializable
                data class InnertubeCommand(val watchEndpoint: ApiWatchEndpoint)
            }
        }
    }
}

@Serializable
data class ApiTrendingContinuation(val continuationContents: ContinuationContents) {
    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionListRenderer<ApiTrending.SectionContent>)
}