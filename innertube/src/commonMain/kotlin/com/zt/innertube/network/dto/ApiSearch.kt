package com.zt.innertube.network.dto

import com.zt.innertube.network.dto.renderer.ChipCloudRenderer
import com.zt.innertube.network.dto.renderer.ElementRenderer
import com.zt.innertube.network.dto.renderer.ItemSectionRenderer
import com.zt.innertube.network.dto.renderer.SectionListRenderer
import com.zt.innertube.serializer.SingletonMapPolymorphicSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule

internal val searchModule = SerializersModule {
    polymorphicDefaultDeserializer(ApiSearch.Model::class ) {
        ApiSearch.UnknownModel.serializer()
    }
}

@Serializable
internal class ApiSearchParams

@Serializable
internal data class ApiSearch(val contents: Contents) {
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
                else -> Unknown.serializer()
            }
        }

        @Serializable
        data class ChipCloud(val chipCloudRenderer: ChipCloudRenderer<Chip>) : Renderer

        @Serializable
        data class CompactChannel(val compactChannelRenderer: CompactChannelRenderer) : Renderer

        @Serializable
        data class Element(val elementRenderer: ElementRenderer<@Serializable(with = Model.Serializer::class) Model>) : Renderer

        @Serializable
        data class Message(val messageRenderer: MessageRenderer) : Renderer

        @Serializable
        object Unknown : Renderer
    }

    @Serializable
    data class Chip(
        @SerialName("chipCloudChipRenderer")
        val chipRenderer: ChipRenderer
    ) {
        @Serializable
        data class ChipRenderer(
            val text: ApiText,
            val navigationEndpoint: NavigationEndpoint? = null
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
        val thumbnail: ApiImage,
        val title: ApiText,
        val videoCountText: ApiText? = null
    )

    @Serializable
    data class MessageRenderer(val text: ApiText)

    @Serializable
    sealed interface Model {
        object Serializer : SingletonMapPolymorphicSerializer<Model>(serializer())
    }

    @Serializable
    @SerialName("videoWithContextSlotsModel")
    data class VideoWithContextSlots(val videoWithContextData: VideoWithContextData) : Model {
        @Serializable
        data class VideoWithContextData(
            val onTap: OnTap<InnertubeCommand>,
            val videoData: Node
        )

        @Serializable
        data class InnertubeCommand(
            val watchEndpoint: WatchEndpoint? = null,
            val browseEndpoint: ApiBrowseEndpoint? = null
        ) {
            @Serializable
            data class WatchEndpoint(
                val continuePlayback: Boolean = false,
                val params: String,
                val playlistId: String? = null,
                val videoId: String? = null
            )
        }

        @Serializable(Node.Serializer::class)
        sealed interface Node {
            companion object Serializer : JsonContentPolymorphicSerializer<Node>(Node::class) {
                override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Node> {
                    val thumbnail = element.jsonObject["thumbnail"]!!.jsonObject

                    return when {
                        thumbnail["isMix"]?.jsonPrimitive?.boolean == true -> Mix.serializer()
                        thumbnail["isPlaylist"]?.jsonPrimitive?.boolean == true -> Playlist.serializer()
                        else -> Video.serializer()
                    }
                }
            }
        }

        @Serializable
        data class Video(
            val avatar: ApiAvatar? = null,
            val decoratedAvatar: DecoratedAvatar? = null,
            val metadata: VideoData.Metadata,
            val thumbnail: ApiThumbnailTimestamp,
            val channelId: String? = null
        ) : Node

        @Serializable
        data class Metadata(val title: String, val byline: String)

        @Serializable
        data class Mix(val metadata: Metadata, val thumbnail: ImageContainer) : Node

        @Serializable
        data class Playlist(val metadata: Metadata, val thumbnail: Thumbnail) : Node {
            @Serializable
            data class Thumbnail(val videoCount: String, val image: ApiImage)
        }
    }

    @Serializable
    @SerialName("videoWithContextModel")
    object VideoWithContext : Model

    @Serializable
    @SerialName("inlineShortsModel")
    object InlineShorts : Model

    @Serializable
    @SerialName("shelfHeaderModel")
    object ShelfHeader : Model

    @Serializable
    @SerialName("postShelfModel")
    object PostShelf : Model

    @Serializable
    @SerialName("horizontalVideoShelfModel")
    object HorizontalVideoShelf : Model

    @Serializable
    @SerialName("hashtagTileModel")
    data class HashtagTile(val renderer: Renderer) : Model {
        @Serializable
        data class Renderer(
            val hashtag: ElementsAttributedStringBox,
            val hashtagChannelCount: ElementsAttributedStringBox? = null,
            val hashtagInfoText: ElementsAttributedStringBox,
            val hashtagThumbnail: HashtagThumbnail,
            val hashtagVideoCount: ElementsAttributedStringBox? = null,
            val hashtagBackgroundColor: Long,
            val onTapCommand: ApiNavigationEndpoint
        ) {
            @Serializable
            data class ElementsAttributedStringBox(val elementsAttributedString: ElementsAttributedString) {
                @Serializable
                data class ElementsAttributedString(val content: String)
            }

            @Serializable
            data class HashtagThumbnail(val elementsImage: ApiImage)
        }
    }

    @Serializable
    @SerialName("cellDividerModel")
    object CellDivider : Model

    @Serializable
    @SerialName("videoDisplayFullLayoutModel")
    object VideoDisplayFullLayout : Model

    @Serializable
    @SerialName("videoDisplayFullButtonedLayoutModel")
    object VideoDisplayFullButtonedLayout : Model

    @Serializable
    @SerialName("shortsShelfModel")
    object ShortsShelf : Model

    @Serializable
    @SerialName("browsyBarModel")
    object BrowsyBar : Model

    @Serializable
    object UnknownModel : Model
}

@Serializable
internal data class ApiSearchContinuation(val continuationContents: ContinuationContents) {
    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionListRenderer<ApiSearch.Content>)
}