package dev.zt64.innertube.network.dto

import dev.zt64.innertube.serializer.DurationAsSecondsSerializer
import dev.zt64.innertube.serializer.SingletonMapPolymorphicSerializer
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.reflect.KClass
import kotlin.time.Duration

internal val nextModule = SerializersModule {
    polymorphicDefaultDeserializer(ApiNext.EngagementPanel::class) {
        ApiNext.UnknownPanel.serializer()
    }
    polymorphicDefaultDeserializer(ApiNext.ItemSection::class) {
        ApiNext.Separator.serializer()
    }
}

@Serializable
internal class CommentParams private constructor(
    @ProtoNumber(2)
    private val context: Context,
    @ProtoNumber(3)
    private val unknown2: Int = 6,
    @ProtoNumber(6)
    private val unknown3: Unknown3
) {
    internal constructor(videoId: String, page: Int) : this(
        context = Context(videoId),
        unknown3 = Unknown3(
            unknown1 = Unknown3.Unknown1(videoId),
            offset = (page * 20) - 20
        )
    )

    @Serializable
    private data class Context(
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
        @EncodeDefault
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
    val engagementPanels: List<@Serializable(EngagementPanelSerializer::class) EngagementPanel>
) {
    @Serializable
    data class Contents(val twoColumnWatchNextResults: WatchNextResults) {
        @Serializable
        data class WatchNextResults(
            val results: @Serializable(ResultsSerializer::class) List<Result>,
            val secondaryResults: @Serializable(SecondaryResultsSerializer::class) List<SecondaryResult>
        ) {
            private object ResultsSerializer : JsonTransformingSerializer<List<Result>>(ListSerializer(Result.serializer())) {
                override fun transformDeserialize(element: JsonElement) = element.jsonObject["results"]!!.jsonObject["contents"]!!
            }

            private object SecondaryResultsSerializer : JsonTransformingSerializer<List<SecondaryResult>>(ListSerializer(SecondaryResult.serializer())) {
                override fun transformDeserialize(element: JsonElement) = element.jsonObject["secondaryResults"]!!.jsonObject["results"]!!
            }
        }
    }

    @Serializable(with = Result.Serializer::class)
    sealed interface Result {
        companion object Serializer : RendererSerializer<Result>(Result::class) {
            override fun selectDeserializer(key: String) = when (key) {
                "videoPrimaryInfoRenderer" -> VideoPrimaryInfoRenderer.serializer()
                "videoSecondaryInfoRenderer" -> VideoSecondaryInfoRenderer.serializer()
                "itemSectionRenderer" -> ItemSection.serializer()
                else -> UnknownRenderer.serializer()
            }
        }
    }

    @Serializable
    @SerialName("videoPrimaryInfoRenderer")
    data class VideoPrimaryInfoRenderer(
        val title: ApiText,
        val viewCount: ViewCount,
        val relativeDateText: SimpleText,
        @Serializable(VideoActionsSerializer::class)
        @SerialName("videoActions")
        val likesText: String
    ) : Result {
        @Serializable
        data class ViewCount(val videoViewCountRenderer: Renderer) {
            @Serializable
            data class Renderer(
                val shortViewCount: SimpleText,
                val viewCount: SimpleText
            )
        }

        private object VideoActionsSerializer : JsonTransformingSerializer<String>(SimpleTextSerializer) {
            override fun transformDeserialize(element: JsonElement) = element
                .jsonObject["menuRenderer"]!!
                .jsonObject["topLevelButtons"]!!
                .jsonArray
                .find { "segmentedLikeDislikeButtonRenderer" in it.jsonObject }!!
                .jsonObject["segmentedLikeDislikeButtonRenderer"]!!
                .jsonObject["likeButton"]!!
                .jsonObject["toggleButtonRenderer"]!!
                .jsonObject["defaultText"]!!
        }
    }

    @Serializable
    @SerialName("videoSecondaryInfoRenderer")
    data class VideoSecondaryInfoRenderer(
        // val description: ApiText? = null,
        // val attributedDescription: String? = null,
        val owner: Owner
    ) : Result {
        @Serializable
        data class Owner(val videoOwnerRenderer: Renderer) {
            @Serializable
            data class Renderer(
                val navigationEndpoint: ApiNavigationEndpoint,
                val subscriberCountText: SimpleText,
                val thumbnail: ApiImage,
                val title: ApiText
            )
        }
    }

    @Serializable
    object UnknownRenderer : Result

    @Serializable
    @JsonClassDiscriminator("sectionIdentifier")
    sealed interface ItemSection : Result

    @Serializable
    object Separator : ItemSection

    @Serializable
    sealed interface SecondaryResultRenderer {
        object Serializer : SingletonMapPolymorphicSerializer<SecondaryResultRenderer>(serializer())
    }

    @Serializable
    data class SecondaryResult(val compactVideoRenderer: CompactVideoRenderer? = null)

    @Serializable
    @SerialName("compactVideoRenderer")
    data class CompactVideoRenderer(
        val channelThumbnail: ApiImage,
        val lengthText: SimpleText? = null,
        val longBylineText: ApiText,
        val publishedTimeText: SimpleText? = null,
        val shortBylineText: ApiText,
        val shortViewCountText: SimpleText,
        val title: SimpleText,
        val videoId: String,
        val viewCountText: SimpleText
    )

    @Serializable
    @JsonClassDiscriminator("panelIdentifier")
    sealed interface EngagementPanel

    private object EngagementPanelSerializer : JsonContentPolymorphicSerializer<EngagementPanel>(EngagementPanel::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<EngagementPanel> {
            val s = when (element.jsonObject["panelIdentifier"]?.jsonPrimitive?.content) {
                "engagement-panel-macro-markers-description-chapters" -> Chapters.serializer()
                else -> UnknownPanel.serializer()
            }

            @Suppress("UNCHECKED_CAST")
            return TransformingSerializer(s as KSerializer<EngagementPanel>)
        }
    }

    private class TransformingSerializer(nextSerializer: KSerializer<EngagementPanel>) : JsonTransformingSerializer<EngagementPanel>(nextSerializer) {
        override fun transformDeserialize(element: JsonElement) = element.jsonObject["engagementPanelSectionListRenderer"]!!
    }

    @Serializable
    @SerialName("engagement-panel-macro-markers-description-chapters")
    data class Chapters(
        @Serializable(ChaptersSerializer::class)
        @SerialName("content")
        val chapters: List<MacroMarkersListItemRenderer>
    ) : EngagementPanel {
        @Serializable
        data class MacroMarkersListItemRenderer(
            val title: SimpleText,
            @Serializable(OnTapSerializer::class)
            @SerialName("onTap")
            val start: Duration,
            val thumbnail: ApiImage,
        ) {
            private object OnTapSerializer : JsonTransformingSerializer<Duration>(DurationAsSecondsSerializer) {
                override fun transformDeserialize(element: JsonElement): JsonElement {
                    return element.jsonObject["watchEndpoint"]!!.jsonObject["startTimeSeconds"]!!
                }
            }
        }

        private object ChaptersSerializer :
            JsonTransformingSerializer<List<MacroMarkersListItemRenderer>>(ListSerializer(MacroMarkersListItemRenderer.serializer())) {
            override fun transformDeserialize(element: JsonElement) = JsonArray(
                element.jsonObject["macroMarkersListRenderer"]!!.jsonObject["contents"]!!.jsonArray.map {
                    it.jsonObject["macroMarkersListItemRenderer"]!!
                }
            )
        }
    }

    @Serializable
    object UnknownPanel : EngagementPanel
}

internal typealias ApiNextContinuation = Continuation<ApiNextContinuationContent>

@Serializable
internal class ApiNextContinuationContent

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
internal abstract class RendererSerializer<T : Any>(
    private val baseClass: KClass<T>
) : KSerializer<T> {
    override val descriptor = buildSerialDescriptor(
        serialName = "JsonContentPolymorphicSerializer<${baseClass.simpleName}>",
        kind = PolymorphicKind.SEALED
    )

    final override fun serialize(encoder: Encoder, value: T) {
        val actualSerializer =
            encoder.serializersModule.getPolymorphic(baseClass, value)
                ?: value::class.serializerOrNull()
                ?: throwSubtypeNotRegistered(value::class, baseClass)
        @Suppress("UNCHECKED_CAST")
        (actualSerializer as KSerializer<T>).serialize(encoder, value)
    }

    final override fun deserialize(decoder: Decoder): T {
        decoder as JsonDecoder

        val (key, value) = decoder.decodeJsonElement().jsonObject.entries.single()

        val actualSerializer = selectDeserializer(key) as KSerializer<T>
        return decoder.json.decodeFromJsonElement(actualSerializer, value)
    }

    protected abstract fun selectDeserializer(key: String): DeserializationStrategy<T>

    private fun throwSubtypeNotRegistered(subClass: KClass<*>, baseClass: KClass<*>): Nothing {
        val subClassName = subClass.simpleName ?: "$subClass"
        val scope = "in the scope of '${baseClass.simpleName}'"
        throw SerializationException(
            "Class '${subClassName}' is not registered for polymorphic serialization $scope.\n" +
                "Mark the base class as 'sealed' or register the serializer explicitly."
        )
    }
}