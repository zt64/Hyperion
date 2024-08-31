package dev.zt64.hyperion.api.network.dto.browse

import dev.zt64.hyperion.api.network.dto.ApiImage
import dev.zt64.hyperion.api.network.dto.ApiText
import dev.zt64.hyperion.api.network.dto.Continuation
import dev.zt64.hyperion.api.network.dto.SimpleText
import dev.zt64.hyperion.api.util.get
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
        override val descriptor =
            PrimitiveSerialDescriptor(serializer().descriptor.serialName, PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: ChannelTab) {
            encoder.encodeString(
                Json.encodeToJsonElement(serializer(), value).jsonPrimitive.content
            )
        }

        override fun deserialize(decoder: Decoder): ChannelTab = Json.decodeFromJsonElement(
            JsonPrimitive(decoder.decodeString())
        )
    }
}

@Serializable
internal data class ChannelParams(
    @ProtoNumber(2)
    val tab:
    @Serializable(ChannelTab.Serializer::class)
    ChannelTab
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
    val metadata: Metadata,
    override val contents: Contents<Content>
) : ApiBrowse() {
    @OptIn(InternalSerializationApi::class)
    @KeepGeneratedSerializer
    @Serializable(Header.HeaderSerializer::class)
    data class Header(
        val title: String,
        val description: String,
        val avatar: ApiImage,
        val channelId: String,
        val badges: List<Badge> = emptyList(),
        val banner: ApiImage? = null,
        val channelHandleText: ApiText,
        val headerLinks: HeaderLinks? = null,
        val subscriberCountText: SimpleText? = null,
        val videosCountText: SimpleText
    ) {
        internal class HeaderSerializer : JsonTransformingSerializer<Header>(serializer()) {
            override fun transformDeserialize(element: JsonElement): JsonElement {
                val vm = element.jsonObject["pageHeaderRenderer"]["content"]["pageHeaderViewModel"]!!
                return buildJsonObject {
                    put("title", vm.jsonObject["title"]["dynamicTextViewModel"]["text"]["content"]!!)
                    put("description", vm.jsonObject["description"]["descriptionPreviewViewModel"]["description"]["content"]!!)
                    put("avatar", vm.jsonObject["image"]["decoratedAvatarViewModel"]["avatar"]["avatarViewModel"]["image"]!!)
                    put("channelId", vm.jsonObject["channelId"]!!.jsonObject["value"]!!)
                    put("badges", vm.jsonObject["badges"]!!.jsonObject["metadataBadgeRenderer"]!!)
                    put("banner", vm.jsonObject["banner"]!!)
                    put("channelHandleText", vm.jsonObject["channelHandleText"]!!)
                    put("headerLinks", vm.jsonObject["headerLinks"]!!)
                    put("subscriberCountText", vm.jsonObject["subscriberCountText"]!!)
                    put("videosCountText", vm.jsonObject["videosCountText"]!!)
                }
            }
        }

        @Serializable
        data class Text(val dynamicTextViewModel: DynamicTextVM) {
            @Serializable
            data class DynamicTextVM(val text: Text) {
                @Serializable
                data class Text(val content: String)
            }
        }

        private class AvatarVMSerializer : JsonTransformingSerializer<ApiImage>(ApiImage.serializer()) {
            override fun transformDeserialize(element: JsonElement): JsonElement {
                return element["decoratedAvatarViewModel"]["avatar"]["avatarViewModel"]!!
            }
        }

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
        data class HeaderLinks(val channelHeaderLinksRenderer: ChannelHeaderLinksRenderer? = null) {
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

    @Serializable
    data class Metadata(
        val title: String,
        val description: String,
        val externalId: String
    )

    @Serializable
    class Content
}

internal typealias ApiChannelContinuation = Continuation<ApiChannel.Content>