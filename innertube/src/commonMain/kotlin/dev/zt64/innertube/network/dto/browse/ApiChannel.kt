package dev.zt64.innertube.network.dto.browse

import dev.zt64.innertube.network.dto.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
enum class ChannelTab {
    @SerialName("home")
    HOME,

    @SerialName("videos")
    VIDEOS,

    @SerialName("shorts")
    SHORTS,

    @SerialName("live")
    LIVE,

    @SerialName("playlists")
    PLAYLISTS,

    @SerialName("community")
    COMMUNITY,

    @SerialName("store")
    STORE,

    @SerialName("channels")
    CHANNELS,

    @SerialName("about")
    ABOUT;

    internal object Serializer : KSerializer<ChannelTab> {
        override val descriptor = PrimitiveSerialDescriptor(serializer().descriptor.serialName, PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: ChannelTab) {
            encoder.encodeString(Json.encodeToJsonElement(serializer(), value).jsonPrimitive.content)
        }

        override fun deserialize(decoder: Decoder): ChannelTab {
            return Json.decodeFromJsonElement(JsonPrimitive(decoder.decodeString()))
        }
    }
}

@Serializable
internal data class ChannelParams(
    @ProtoNumber(2)
    val tab: @Serializable(ChannelTab.Serializer::class) ChannelTab
)

@Serializable
internal data class ChannelContinuation(
    @ProtoNumber(80226972)
    val context: Context
) {
    @Serializable
    class Context(
        @ProtoNumber(2)
        val channelId: String,
        @ProtoNumber(3)
        val proto: String
    )

    @Serializable
    class More(
        @ProtoNumber(110)
        val params: Params
    ) {
        @Serializable
        class Params(
            @ProtoNumber(15)
            val unknown: Unknown
        ) {
            @Serializable
            class Unknown(
                @ProtoNumber(1)
                val guh: Guh,
                @ProtoNumber(3)
                val sort: Sort
            ) {
                @Serializable
                class Guh(
                    @ProtoNumber(1)
                    val unknown: String,
                    @ProtoNumber(2)
                    val uuid: String
                )
            }
        }
    }
}

@Serializable
internal enum class Sort {
    @ProtoNumber(1)
    RECENTLY_UPLOADED,

    @ProtoNumber(2)
    POPULAR
}

@Serializable
internal data class ApiChannel(
    val header: Header,
    override val contents: Contents<Content>
) : ApiBrowse() {
    @Serializable
    data class Header(val c4TabbedHeaderRenderer: C4TabbedHeaderRenderer) {
        @Serializable
        data class C4TabbedHeaderRenderer(
            val channelId: String,
            val avatar: ApiImage,
            val badges: List<Badge> = emptyList(),
            val banner: ApiImage? = null,
            val channelHandleText: ApiText,
            val headerLinks: HeaderLinks? = null,
            val subscriberCountText: SimpleText? = null,
            val title: String,
            val videosCountText: SimpleText
        ) {
            @Serializable
            data class Badge(
                @SerialName("metadataBadgeRenderer")
                @Serializable(TypeSerializer::class)
                val type: String
            ) {
                private object TypeSerializer : JsonTransformingSerializer<String>(String.serializer()) {
                    override fun transformDeserialize(element: JsonElement): JsonElement {
                        return element.jsonObject["icon"]!!.jsonObject["iconType"]!!
                    }
                }
            }

            @Serializable
            data class HeaderLinks(val channelHeaderLinksRenderer: ChannelHeaderLinksRenderer) {
                @Serializable
                data class ChannelHeaderLinksRenderer(
                    val primaryLinks: List<Link> = emptyList(),
                    val secondaryLinks: List<Link> = emptyList()
                ) {
                    @Serializable
                    data class Link(
                        val icon: ApiImage,
                        @Serializable(EndpointSerializer::class)
                        val navigationEndpoint: String,
                        val title: SimpleText
                    ) {
                        private object EndpointSerializer : JsonTransformingSerializer<String>(String.serializer()) {
                            override fun transformDeserialize(element: JsonElement): JsonElement {
                                return element.jsonObject["urlEndpoint"]!!.jsonObject["url"]!!
                            }
                        }
                    }
                }
            }
        }
    }

    @Serializable
    class Content
}

internal typealias ApiChannelContinuation = Continuation<ApiChannel.Content>