package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ListRenderer
import kotlinx.serialization.Serializable

@Serializable
data class ApiChannel(
    val contents: Contents,
    val header: Header
) {
    @Serializable
    data class Contents(val singleColumnBrowseResultsRenderer: SingleColumnBrowseResultsRenderer)

    @Serializable
    data class SingleColumnBrowseResultsRenderer(val tabs: List<Tab>) {
        @Serializable
        data class Tab(val tabRenderer: TabRenderer) {
            @Serializable
            data class TabRenderer(
                val endpoint: Endpoint,
                val content: Content,
                val selected: Boolean,
                val title: String
            ) {
                @Serializable
                data class Endpoint(val browseEndpoint: ApiBrowseEndpoint)

                @Serializable
                data class Content(val sectionListRenderer: SectionListRenderer) {
                    @Serializable
                    data class SectionListRenderer(val contents: List<Content>? = null) {
                        @Serializable
                        data class Content(val shelfRenderer: ShelfRenderer? = null) {
                            @Serializable
                            data class ShelfRenderer(val content: Content) {
                                @Serializable
                                data class Content(
                                    val horizontalListRenderer: ListRenderer<HorizontalListItem>? = null,
                                    val verticalListRenderer: ListRenderer<VerticalListItem>? = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Serializable
    data class HorizontalListItem(
        val elementRenderer: ElementRenderer<Model>? = null,
        val gridChannelRenderer: GridChannelRenderer? = null
    ) {
        @Serializable
        data class Model(val gridVideoModel: ApiChannelVideo? = null)

        @Serializable
        data class GridChannelRenderer(
            val channelId: String,
            val navigationEndpoint: ApiNavigationEndpoint,
            val shortSubscriberCountText: ApiText? = null,
            val subscriberCountText: ApiText? = null,
            val thumbnail: ApiThumbnail,
            val title: ApiText
        )
    }

    @Serializable
    data class VerticalListItem(val elementRenderer: ElementRenderer<Model>) {
        @Serializable
        data class Model(val videoWithContextModel: ApiVideo)
    }

    @Serializable
    data class Header(val channelMobileHeaderRenderer: ChannelMobileHeaderRenderer) {
        @Serializable
        data class ChannelMobileHeaderRenderer(val channelHeader: ChannelHeader)

        @Serializable
        data class ChannelHeader(val elementRenderer: ElementRenderer<Model>)

        @Serializable
        data class Model(val channelHeaderModel: ChannelHeaderModel)
    }

    @Serializable
    data class ChannelHeaderModel(
        val channelBanner: ChannelBanner? = null,
        val channelProfile: ChannelProfile
    ) {
        @Serializable
        data class ChannelBanner(val image: ApiImage)

        @Serializable
        data class ChannelProfile(
            val avatarData: AvatarData,
            val descriptionPreview: DescriptionPreview,
            val metadata: Metadata,
            val title: String
        ) {
            @Serializable
            data class AvatarData(val avatar: Avatar) {
                @Serializable
                data class Avatar(val image: ApiImage)
            }

            @Serializable
            data class DescriptionPreview(val description: String = "")

            @Serializable
            data class Metadata(
                val joinDateText: String,
                val subscriberCountText: String? = null,
                val videosCountText: String? = null
            )
        }
    }
}

@Serializable
data class ApiChannelVideo(
    val videoData: VideoData,
    val onTap: OnTap
) {
    @Serializable
    data class VideoData(
        val metadata: Metadata,
        val thumbnail: ApiThumbnailTimestamp
    ) {
        @Serializable
        data class Metadata(
            val metadataDetails: String,
            val title: String
        )
    }

    @Serializable
    data class OnTap(val innertubeCommand: InnerTubeCommand) {
        @Serializable
        data class InnerTubeCommand(val watchEndpoint: WatchEndpoint? = null)
    }
}