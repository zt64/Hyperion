package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ChipCloudRenderer
import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ItemSectionRenderer
import com.hyperion.network.dto.renderer.SectionListRenderer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class ApiSearch(val contents: Contents) {
    @Serializable
    data class Contents(val sectionListRenderer: SectionListRenderer<Content>)

    @Serializable
    data class Content(val itemSectionRenderer: ItemSectionRenderer<Renderer>? = null)

    @Serializable(with = Renderer.Serializer::class)
    sealed interface Renderer {
        companion object Serializer : JsonContentPolymorphicSerializer<Renderer>(Renderer::class) {
            override fun selectDeserializer(element: JsonElement) = when (element.jsonObject.keys.single()) {
                "chipCloudRenderer" -> ChipCloud.serializer()
                "compactChannelRenderer" -> CompactChannel.serializer()
                "elementRenderer" -> Element.serializer()
                "messageRenderer" -> Message.serializer()
                else -> throw NoWhenBranchMatchedException(element.jsonObject.toString())
            }
        }

        @Serializable
        data class ChipCloud(val chipCloudRenderer: ChipCloudRenderer<Chip>) : Renderer

        @Serializable
        data class CompactChannel(val compactChannelRenderer: CompactChannelRenderer) : Renderer

        @Serializable
        data class Element(val elementRenderer: ElementRenderer<Model>) : Renderer

        @Serializable
        data class Message(val messageRenderer: MessageRenderer) : Renderer
    }

    @Serializable
    data class Chip(
        @SerialName("chipCloudChipRenderer")
        val chipRenderer: ChipRenderer
    ) {
        @Serializable
        data class ChipRenderer(
            val text: ApiText,
            val navigationEndpoint: NavigationEndpoint? = null,
        ) {
            @Serializable
            data class NavigationEndpoint(val searchEndpoint: SearchEndpoint) {
                @Serializable
                data class SearchEndpoint(
                    val originalChipQuery: String,
                    val params: String,
                    val query: String
                )
            }
        }
    }

    @Serializable
    data class CompactChannelRenderer(
        val channelId: String,
        val displayName: ApiText,
        val navigationEndpoint: ApiNavigationEndpoint,
        val subscriberCountText: ApiText? = null,
        val thumbnail: ApiThumbnail,
        val title: ApiText,
        val videoCountText: ApiText? = null
    )

    @Serializable
    data class MessageRenderer(val text: ApiText)

    @Serializable(with = Model.Serializer::class)
    sealed interface Model {
        companion object Serializer : JsonContentPolymorphicSerializer<Model>(Model::class) {
            override fun selectDeserializer(element: JsonElement) = when (element.jsonObject.entries.single().key) {
                "videoWithContextSlotsModel" -> VideoWithContextSlots.serializer()
                "videoWithContextModel" -> VideoWithContext.serializer()
                "inlineShortsModel" -> InlineShorts.serializer()
                "shelfHeaderModel" -> ShelfHeader.serializer()
                else -> Unknown.serializer()
            }
        }
    }

    @Serializable
    data class VideoWithContextSlots(val videoWithContextSlotsModel: Model) : Model {
        @Serializable
        data class Model(val videoWithContextData: VideoWithContextData)

        @Serializable
        data class VideoWithContextData(
            val onTap: OnTap,
            val videoData: VideoData
        )

        @Serializable
        data class OnTap(val innertubeCommand: InnertubeCommand) {
            @Serializable
            data class InnertubeCommand(val watchEndpoint: WatchEndpoint) {
                @Serializable
                data class WatchEndpoint(
                    val continuePlayback: Boolean = false,
                    val params: String,
                    val playlistId: String? = null,
                    val videoId: String? = null
                )
            }
        }

        @Serializable
        data class VideoData(
            val avatar: ApiAvatar? = null,
            val metadata: Metadata,
            val thumbnail: Thumbnail
        ) {
            @Serializable
            data class Metadata(
                val title: String,
                val byline: String? = null,
                val metadataDetails: String? = null,
                val isPlaylistMix: Boolean = false,
                val isVideoWithContext: Boolean? = null,
            )

            @Serializable
            data class Thumbnail(
                val isMix: Boolean? = null,
                val isPlaylist: Boolean? = null,
                val isVideoWithContext: Boolean,
                val timestampText: String? = null,
                val videoCount: String? = null
            )
        }
    }

    @Serializable
    object VideoWithContext : Model

    @Serializable
    object InlineShorts : Model

    @Serializable
    object ShelfHeader : Model

    @Serializable
    object Unknown : Model
}

@Serializable
data class ApiSearchContinuation(val continuationContents: ContinuationContents) {
    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionListRenderer<ApiSearch.Content>)
}