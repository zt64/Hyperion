package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ListRenderer
import com.hyperion.network.dto.renderer.SectionListRenderer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiChannel(
    val contents: Contents,
    val header: Header
) {
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
            data class TabRenderer(
                val endpoint: Endpoint,
                val content: TabContent,
                val selected: Boolean = false,
                val title: String
            ) {
                @Serializable
                data class Endpoint(val browseEndpoint: ApiBrowseEndpoint)
            }

            @Serializable
            data class TabContent(val sectionListRenderer: SectionListRenderer<Content>) {
                @Serializable
                data class Content(val shelfRenderer: ShelfRenderer? = null) {
                    @Serializable
                    data class ShelfRenderer(val content: ShelfRendererContent)
                }
            }
        }
    }

    @Serializable
    data class ShelfRendererContent(
        val horizontalListRenderer: ListRenderer<HorizontalListItem>? = null,
        val verticalListRenderer: ListRenderer<VerticalListItem>? = null
    ) {
        @Serializable
        data class HorizontalListItem(
            val elementRenderer: ElementRenderer<Model>? = null,
            val gridChannelRenderer: GridChannelRenderer? = null
        ) {
            @Serializable
            data class Model(val gridVideoModel: ApiVideo.ContextData? = null)

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
            data class Model(val videoWithContextModel: ApiNextVideo)
        }
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