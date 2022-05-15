package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiTrending(
    val contents: Contents,
    val continuationContents: ContinuationContents? = null
) {
    @Serializable
    data class Contents(val singleColumnBrowseResultsRenderer: SingleColumnBrowseResultsRenderer) {
        @Serializable
        data class SingleColumnBrowseResultsRenderer(val tabs: List<Tab>) {
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
                data class Content(val videoWithContextRenderer: ApiVideoContext? = null)
            }
        }
    }

    @Serializable
    data class ApiVideoContext(
        val channelThumbnail: ApiChannelThumbnail,
        val headline: ApiText,
        val isWatched: Boolean,
        val publishedTimeText: ApiText,
        val shortBylineText: ShortBylineText,
        val shortViewCountText: ApiText? = null,
        val lengthText: ApiText,
        val thumbnail: ApiThumbnail,
        val videoId: String
    ) {
        @Serializable
        data class ApiChannelThumbnail(val channelThumbnailWithLinkRenderer: ChannelThumbnailWithLinkRenderer) {
            @Serializable
            data class ChannelThumbnailWithLinkRenderer(val thumbnail: ApiThumbnail)
        }

        @Serializable
        data class ShortBylineText(val runs: List<Run>) {
            @Serializable
            data class Run(
                val text: String,
                val navigationEndpoint: NavigationEndpoint
            ) {
                @Serializable
                data class NavigationEndpoint(val browseEndpoint: BrowseEndpoint) {
                    @Serializable
                    data class BrowseEndpoint(val browseId: String)
                }
            }
        }
    }
}