package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ChipCloudRenderer
import com.hyperion.network.dto.renderer.ElementRenderer
import com.hyperion.network.dto.renderer.ItemSectionRenderer
import com.hyperion.network.dto.renderer.SectionListRenderer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

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
        data class Element(val elementRenderer: ElementRenderer<Model>) : Renderer

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
                "hashtagTileModel" -> HashtagTile.serializer()
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

        @Serializable(with = Node.Serializer::class)
        sealed interface Node {
            companion object Serializer : JsonContentPolymorphicSerializer<Node>(Node::class) {
                override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Node> {
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
        data class Mix(
            val metadata: Metadata,
            val thumbnail: ImageContainer
        ) : Node {
            @Serializable
            data class Metadata(
                val title: String,
                val byline: String
            )
        }

        @Serializable
        data class Playlist(
            val metadata: Metadata,
            val thumbnail: Thumbnail
        ) : Node {
            @Serializable
            data class Metadata(
                val title: String,
                val byline: String
            )

            @Serializable
            data class Thumbnail(
                val videoCount: String,
                val image: ApiImage
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
    data class HashtagTile(val hashtagTileModel: HashtagTileModel) : Model {
        @Serializable
        data class HashtagTileModel(val renderer: Renderer) {
            @Serializable
            data class Renderer(
                val hashtag: ElementsAttributedStringBox,
                val hashtagChannelCount: ElementsAttributedStringBox,
                val hashtagInfoText: ElementsAttributedStringBox,
                val hashtagThumbnail: HashtagThumbnail,
                val hashtagVideoCount: ElementsAttributedStringBox,
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
    }

    @Serializable
    object Unknown : Model
}

@Serializable
internal data class ApiSearchContinuation(val continuationContents: ContinuationContents) {
    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionListRenderer<ApiSearch.Content>)
}