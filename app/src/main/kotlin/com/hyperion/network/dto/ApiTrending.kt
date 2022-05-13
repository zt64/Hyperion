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
                data class TabRenderer(val content: TabContent? = null)
            }
        }
    }

    @Serializable
    data class TabContent(val sectionListRenderer: SectionList)

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
        val headline: Headline,
        val isWatched: Boolean,
        val publishedTimeText: PublishedTimeText,
        val shortBylineText: ShortBylineText,
        val shortViewCountText: ShortViewCountText? = null,
        val lengthText: LengthText,
        val thumbnail: Thumbnail,
        val videoId: String
    ) {
        @Serializable
        data class ApiChannelThumbnail(val channelThumbnailWithLinkRenderer: ChannelThumbnailWithLinkRenderer) {
            @Serializable
            data class ChannelThumbnailWithLinkRenderer(val thumbnail: Thumbnail)
        }

        @Serializable
        data class Headline(val runs: TextRuns)

        @Serializable
        data class PublishedTimeText(val runs: TextRuns)

        @Serializable
        data class LengthText(val runs: TextRuns)

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
                    data class BrowseEndpoint(
                        val browseId: String,
                        val canonicalBaseUrl: String
                    )
                }
            }
        }

        @Serializable
        data class ShortViewCountText(val runs: TextRuns)

        @Serializable
        data class Thumbnail(val thumbnails: List<Thumbnail>) {
            @Serializable
            data class Thumbnail(
                val width: Int,
                val height: Int,
                val url: String
            )
        }
    }
}