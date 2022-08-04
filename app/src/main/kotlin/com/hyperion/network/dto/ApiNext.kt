package com.hyperion.network.dto

import com.hyperion.network.dto.renderer.ElementRenderer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
data class ApiNext(
    val contents: Contents? = null,
    val engagementPanels: List<EngagementPanel> = emptyList(),
    val continuationContents: ContinuationContents? = null,
) {
    @Serializable
    data class Contents(val singleColumnWatchNextResults: SingleColumnWatchNextResults) {
        @Serializable
        data class SingleColumnWatchNextResults(val results: Results) {
            @Serializable
            data class Results(val results: Results) {
                @Serializable
                data class Results(
                    val contents: List<Renderer>,
                    val continuations: List<ApiContinuation>
                ) {
                    @Serializable(with = Renderer.Serializer::class)
                    abstract class Renderer {
                        companion object Serializer : KSerializer<Renderer> {
                            override val descriptor = buildClassSerialDescriptor("Renderer")

                            override fun deserialize(decoder: Decoder): Renderer {
                                val input = (decoder as? JsonDecoder)!!
                                val tree = input.decodeJsonElement().jsonObject
                                val json = input.json

                                return when {
                                    "slimVideoMetadataSectionRenderer" in tree ->
                                        json.decodeFromJsonElement(SlimVideoMetadataSectionRenderer.serializer(), tree["slimVideoMetadataSectionRenderer"]!!)
                                    "itemSectionRenderer" in tree ->
                                        json.decodeFromJsonElement(ItemSectionRenderer.serializer(), tree["itemSectionRenderer"]!!)
                                    "shelfRenderer" in tree ->
                                        json.decodeFromJsonElement(ShelfRenderer.serializer(), tree["shelfRenderer"]!!)
                                    else -> throw NoWhenBranchMatchedException()
                                }
                            }

                            override fun serialize(encoder: Encoder, value: Renderer) {
                                TODO("Not yet implemented")
                            }
                        }
                    }

                    @Serializable
                    data class SlimVideoMetadataSectionRenderer(val contents: List<Content>) : Renderer() {
                        @Serializable
                        data class Content(val elementRenderer: ElementRenderer<Model>)
                    }

                    @Serializable
                    class ShelfRenderer : Renderer()

                    @Serializable(with = ItemSectionRenderer.Serializer::class)
                    abstract class ItemSectionRenderer : Renderer() {
                        companion object Serializer :
                            JsonContentPolymorphicSerializer<ItemSectionRenderer>(ItemSectionRenderer::class) {
                            override fun selectDeserializer(
                                element: JsonElement
                            ): DeserializationStrategy<out ItemSectionRenderer> {
                                val sectionIdentifier = element.jsonObject["sectionIdentifier"]
                                    ?.jsonPrimitive
                                    ?.contentOrNull

                                return when (sectionIdentifier) {
                                    "comments-entry-point" -> CommentsEntryRenderer.serializer()
                                    "related-items" -> RelatedItemsRenderer.serializer()
                                    null -> SeparatorRenderer.serializer()
                                    else -> throw NoWhenBranchMatchedException()
                                }
                            }
                        }
                    }

                    @Serializable
                    data class CommentsEntryRenderer(val contents: List<Content>) : ItemSectionRenderer() {
                        @Serializable
                        data class Content(val elementRenderer: ElementRenderer<Model>)

                        @Serializable
                        class Model
//                        @Serializable
//                        data class Model(val commentsCompositeEntryPointModel: ApiVideo)
                    }

                    @Serializable
                    class RelatedItemsRenderer(val contents: List<Content>) : ItemSectionRenderer() {
                        @Serializable
                        data class Content(val elementRenderer: ElementRenderer<Model>)

                        @Serializable
                        data class Model(val videoWithContextModel: ApiVideo? = null)
                    }

                    @Serializable
                    class SeparatorRenderer : ItemSectionRenderer()
                }
            }
        }
    }

    @Serializable
    data class EngagementPanel(
        val panelIdentifier: PanelIdentifier? = null,
        val header: Header? = null,
        val content: Content? = null
    ) {
        @Serializable
        enum class PanelIdentifier {
            @SerialName("video-description-ep-identifier")
            DESCRIPTION,

            @SerialName("comment-item-section")
            COMMENTS
        }

        @Serializable
        data class Header(
            @SerialName("engagementPanelHeaderRenderer")
            val renderer: Renderer
        ) {
            @Serializable
            data class Renderer(
                val title: ApiText,
                val contextualInfo: ApiText
            )
        }

        @Serializable
        data class Content(
            @SerialName("sectionListRenderer")
            val renderer: SectionListRenderer<Model>
        ) {
            @Serializable
            sealed interface Model {
                @Serializable
                data class VideoDescriptionHeaderModel(val videoDescriptionHeader: VideoDescriptionHeader) : Model {
                    @Serializable
                    data class VideoDescriptionHeader(
                        private val videoTitle: JsonObject,
                        val channelName: String,
                        val viewCountText: String,
                        val dateText: String
                    ) {
                        val title = videoTitle.jsonPrimitive.content
                    }
                }
            }
        }
    }

    @Serializable
    data class SectionListRenderer<T>(
        private val contents: List<SectionContent<T>> = emptyList(),
        val continuations: List<ApiContinuation> = emptyList()
    ) {
        @Serializable
        data class SectionContent<T>(val itemSectionRenderer: ItemSectionRenderer<T>? = null) {
            @Serializable
            data class ItemSectionRenderer<T>(val contents: List<Content<T>>) {
                @Serializable
                data class Content<T>(val elementRenderer: ElementRenderer<T>? = null)
            }
        }
    }

    @Serializable
    data class ContinuationContents(val sectionListContinuation: SectionListContinuation) {
        @Serializable
        data class Model(val videoWithContextModel: VideoWithContextModel) {
            @Serializable
            data class VideoWithContextModel(val videoWithContextData: VideoWithContextData) {
                @Serializable
                data class VideoWithContextData(
                    val onTap: OnTap,
                    val videoData: VideoData
                ) {
                    @Serializable
                    data class OnTap(val innertubeCommand: InnertubeCommand) {
                        @Serializable
                        data class InnertubeCommand(
                            val watchNextWatchEndpointMutationCommand: WatchNextWatchEndpointMutationCommand
                        ) {
                            @Serializable
                            data class WatchNextWatchEndpointMutationCommand(val watchEndpoint: ApiWatchEndpoint)
                        }
                    }

                    @Serializable
                    data class VideoData(
                        val avatar: Avatar? = null,
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
                            val metadataDetails: String = "YouTube",
                            val title: String
                        )

                        @Serializable
                        data class Thumbnail(
                            val image: ApiImage,
                            val timestampText: String? = null
                        )
                    }
                }
            }
        }

        @Serializable
        data class SectionListContinuation(
            val contents: List<Content>,
            val continuations: List<ApiContinuation>
        ) {
            @Serializable
            data class Content(val itemSectionRenderer: ItemSectionRenderer) {
                @Serializable
                data class ItemSectionRenderer(val contents: List<Content>) {
                    @Serializable
                    data class Content(val elementRenderer: ElementRenderer<Model>)
                }
            }
        }
    }

    @Serializable
    data class Model(
        val videoMetadataModel: VideoMetadataModel? = null,
        val channelBarModel: ChannelBarModel? = null,
        val videoActionBarModel: VideoActionBarModel? = null
    ) {
        @Serializable
        data class VideoMetadataModel(val videoMetadata: VideoMetadata) {
            @Serializable
            data class VideoMetadata(
                val subtitleData: SubtitleData,
                val title: Text
            ) {
                @Serializable
                data class SubtitleData(
                    val viewCount: Text,
                    val dateA11yLabel: String
                )

                @Serializable
                data class Text(val content: String)
            }
        }

        @Serializable
        data class ChannelBarModel(val videoChannelBarData: VideoChannelBarData) {
            @Serializable
            data class VideoChannelBarData(
                val avatar: ApiAvatar,
                val subtitle: String? = null
            )
        }

        @Serializable
        data class VideoActionBarModel(val buttons: List<Button>)
    }

    @Serializable(with = Button.Serializer::class)
    sealed interface Button {
        companion object Serializer : KSerializer<Button> {
            override val descriptor = buildClassSerialDescriptor("Button")

            override fun deserialize(decoder: Decoder): Button {
                val input = (decoder as? JsonDecoder)!!
                val tree = input.decodeJsonElement().jsonObject
                val json = input.json

                return when {
                    "likeButton" in tree -> json.decodeFromJsonElement(LikeButton.serializer(), tree["likeButton"]!!)
                    "dislikeButton" in tree -> json.decodeFromJsonElement(DislikeButton.serializer(), tree["dislikeButton"]!!)
                    else -> OtherButton
                }
            }

            override fun serialize(encoder: Encoder, value: Button) = TODO("Not yet implemented")
        }

        @Serializable
        data class ButtonData(val defaultButton: DefaultButton) {
            @Serializable
            data class DefaultButton(val title: String)
        }

        @Serializable
        data class LikeButton(val buttonData: ButtonData) : Button

        @Serializable
        data class DislikeButton(val buttonData: ButtonData) : Button

        @Serializable
        object OtherButton : Button
    }
}