package com.zt.innertube.network.dto

import com.zt.innertube.network.dto.browse.ApiBrowseContinuation
import com.zt.innertube.network.dto.renderer.ElementRenderer
import com.zt.innertube.network.dto.renderer.ItemSectionRenderer
import com.zt.innertube.network.dto.renderer.SectionListRenderer
import com.zt.innertube.serializer.DurationAsSecondsSerializer
import com.zt.innertube.serializer.SingletonMapPolymorphicSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.time.Duration

internal val nextModule = SerializersModule {
    polymorphicDefaultDeserializer(ApiNext.EngagementPanel::class) {
        ApiNext.UnknownPanel.serializer()
    }
    polymorphicDefaultDeserializer(ApiNext.Renderer.ItemSection::class) {
        ApiNext.Renderer.Separator.serializer()
    }
}

@Serializable
internal class CommentParams private constructor(
    @ProtoNumber(2)
    private val unknown1: Unknown1,
    @ProtoNumber(3)
    private val unknown2: Int = 6,
    @ProtoNumber(6)
    private val unknown3: Unknown3
) {
    internal constructor(videoId: String, page: Int) : this(
        unknown1 = Unknown1(videoId),
        unknown3 = Unknown3(
            unknown1 = Unknown3.Unknown1(videoId),
            offset = (page * 20) - 20
        )
    )

    @Serializable
    private data class Unknown1(
        @ProtoNumber(2)
        val videoId: String
    )

    @Serializable
    private data class Unknown3(
        @ProtoNumber(4)
        val unknown1: Unknown1,
        @ProtoNumber(5)
        val offset: Int,
        @ProtoNumber(8)
        val section: String = "engagement-panel-comments-section"
    ) {
        @Serializable
        data class Unknown1(
            @ProtoNumber(4)
            val videoId: String
        )
    }
}

@Serializable
internal data class ApiNext(
    val contents: Contents,
    val engagementPanels: List<@Serializable(EngagementPanel.Serializer::class) EngagementPanel>
) {
    @Serializable(with = Renderer.Serializer::class)
    sealed interface Renderer {
        companion object Serializer : KSerializer<Renderer> {
            override val descriptor = buildClassSerialDescriptor("Renderer")

            override fun deserialize(decoder: Decoder): Renderer {
                val input = decoder as JsonDecoder
                val tree = input.decodeJsonElement().jsonObject

                val serializer = when (tree.keys.single()) {
                    "slimVideoMetadataSectionRenderer" -> SlimVideoMetadataSection.serializer()
                    "itemSectionRenderer" -> ItemSection.serializer()
                    "shelfRenderer" -> Shelf.serializer()
                    else -> throw NoWhenBranchMatchedException()
                }

                return input.json.decodeFromJsonElement(serializer, tree.values.single())
            }

            override fun serialize(encoder: Encoder, value: Renderer): Unit = TODO("Not yet implemented")
        }

        @Serializable
        data class SlimVideoMetadataSection(val contents: List<Content>) : Renderer {
            @Serializable
            data class Content(val elementRenderer: ElementRenderer<Model>)
        }

        @Serializable
        object Shelf : Renderer

        @Serializable
        @JsonClassDiscriminator("sectionIdentifier")
        sealed interface ItemSection : Renderer

        @Serializable
        @SerialName("comments-entry-point")
        data class CommentsEntry(val contents: List<Content>) : ItemSection {
            @Serializable
            data class Content(val elementRenderer: ElementRenderer<Model>)

            @Serializable
            data class Model(val commentsCompositeEntryPointModel: EntryPointModel) {
                @Serializable
                data class EntryPointModel(val data: Data) {
                    @Serializable
                    data class Data(val teasers: List<Teaser> = emptyList()) {
                        @Serializable
                        data class Teaser(
                            val avatar: ImageContainer,
                            val teaserText: TeaserText
                        ) {
                            @Serializable
                            data class TeaserText(val content: String)
                        }
                    }
                }
            }
        }

        @Serializable
        @SerialName("related-items")
        data class RelatedItems(val contents: List<Content>) : ItemSection {
            @Serializable
            data class Content(val elementRenderer: ElementRenderer<Model>)

            @Serializable
            data class Model(val videoWithContextModel: ApiNextVideo? = null)
        }

        @Serializable
        object Separator : ItemSection
    }

    @Serializable
    data class Contents(val singleColumnWatchNextResults: SingleColumnWatchNextResults) {
        @Serializable
        data class SingleColumnWatchNextResults(val results: Results) {
            @Serializable
            data class Results(val results: SectionListRenderer<Renderer>)
        }
    }

    @Serializable
    data class Model(
        val videoMetadataModel: VideoMetadataModel? = null,
        val channelBarModel: ChannelBarModel? = null,
        val videoActionBarModel: VideoActionBarModel? = null
    ) {
        @Serializable
        data class VideoMetadataModel(val videoMetadata: VideoMetadata)

        @Serializable
        data class ChannelBarModel(val videoChannelBarData: VideoChannelBarData)

        @Serializable
        data class VideoActionBarModel(
            val buttons: List<@Serializable(with = Button.Serializer::class) Button>
        )
    }

    @Serializable
    data class VideoMetadata(
        val title: Text,
        val subtitleData: SubtitleData,
        val badgesData: List<Badge> = emptyList()
    ) {
        @Serializable
        data class Text(val content: String)

        @Serializable
        data class SubtitleData(
            val viewCount: Text,
            val date: Text
        )

        @Serializable
        data class Badge(val label: String)
    }

    @Serializable
    data class VideoChannelBarData(
        val avatar: ImageContainer,
        val subtitle: String? = null
    )

    @Serializable
    sealed interface Button {
        object Serializer : KSerializer<Button> by SingletonMapPolymorphicSerializer(serializer())

        @Serializable
        data class ButtonData(val defaultButton: DefaultButton) {
            @Serializable
            data class DefaultButton(val title: String)
        }

        @Serializable
        @SerialName("likeButton")
        data class LikeButton(val buttonData: ButtonData) : Button

        @Serializable
        @SerialName("dislikeButton")
        data class DislikeButton(val buttonData: ButtonData) : Button

        @Serializable
        @SerialName("actionButton")
        object ActionButton : Button

        @Serializable
        @SerialName("downloadButton")
        object DownloadButton : Button

        @Serializable
        @SerialName("saveToPlaylistButton")
        object SaveToPlaylistButton : Button
    }

    @Serializable
    @JsonClassDiscriminator("panelIdentifier")
    sealed interface EngagementPanel {
        object Serializer : TransformingSerializer(serializer())

        open class TransformingSerializer(nextSerializer: KSerializer<EngagementPanel>) : JsonTransformingSerializer<EngagementPanel>(nextSerializer) {
            override fun transformDeserialize(element: JsonElement): JsonElement {
                return element.jsonObject["engagementPanelSectionListRenderer"]!!
            }
        }
    }

    @Serializable
    @SerialName("engagement-panel-macro-markers-description-chapters")
    class Chapters(val content: Content) : EngagementPanel {
        @Serializable
        data class Content(val sectionListRenderer: SectionListRenderer<Content>) {
            @Serializable
            data class Content(val itemSectionRenderer: ItemSectionRenderer<Content>) {
                @Serializable
                data class Content(val elementRenderer: ElementRenderer<Model>)
            }
        }

        @Serializable
        data class Model(val macroMarkersListItemModel: MacroMarkersListItemModel? = null) {
            @Serializable
            data class MacroMarkersListItemModel(val renderer: Renderer) {
                @Serializable
                data class Renderer(
                    val onTap: OnTap,
                    val thumbnail: Thumbnail,
                    val timeDescription: Title,
                    val title: Title
                ) {
                    @Serializable
                    data class OnTap(val watchEndpoint: WatchEndpoint) {
                        @Serializable
                        data class WatchEndpoint(
                            val continuePlayback: Boolean,
                            @Serializable(with = DurationAsSecondsSerializer::class)
                            @SerialName("startTimeSeconds")
                            val startTime: Duration,
                            val videoId: String
                        )
                    }

                    @Serializable
                    data class Thumbnail(val elementsImage: ApiImage)

                    @Serializable
                    data class Title(val elementsAttributedString: ElementsAttributedString) {
                        @Serializable
                        data class ElementsAttributedString(val content: String)
                    }
                }
            }
        }
    }

    @Serializable
    @SerialName("video-description-ep-identifier")
    object VideoDescription : EngagementPanel

    @Serializable
    @SerialName("comment-item-section")
    object CommentItemSection : EngagementPanel

    @Serializable
    @SerialName("live-chat-item-section")
    object LiveChatItemSection : EngagementPanel

    @Serializable
    @SerialName("engagement-panel-searchable-transcript")
    object SearchableTranscript : EngagementPanel

    @Serializable
    object UnknownPanel : EngagementPanel
}

@Serializable
internal data class ApiNextContinuation(
    override val continuationContents: ContinuationContents<Content>
) : ApiBrowseContinuation() {
    @Serializable
    data class Content(val itemSectionRenderer: ItemSectionRenderer<Content>) {
        @Serializable
        data class Content(val elementRenderer: ElementRenderer<Model>)
    }

    @Serializable
    data class Model(val videoWithContextModel: ApiNextVideo? = null)
}