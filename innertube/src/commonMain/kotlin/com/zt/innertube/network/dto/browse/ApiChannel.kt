package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
internal enum class ChannelTab {
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

    @SerialName("about")
    ABOUT;

    object Serializer : KSerializer<ChannelTab> {
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
internal data class Continuation(
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
            val badges: List<Badge>,
            val banner: ApiImage,
            val channelHandleText: ApiText,
            val headerLinks: HeaderLinks,
            val subscriberCountText: SimpleText,
            val title: String,
            val videosCountText: SimpleText
        ) {
            @Serializable
            data class Badge(val metadataBadgeRenderer: MetadataBadgeRenderer) {
                @Serializable
                data class MetadataBadgeRenderer(val icon: Icon) {
                    @Serializable
                    data class Icon(val iconType: String)
                }
            }

            @Serializable
            data class HeaderLinks(val channelHeaderLinksRenderer: ChannelHeaderLinksRenderer) {
                @Serializable
                data class ChannelHeaderLinksRenderer(
                    val primaryLinks: List<Link>,
                    val secondaryLinks: List<Link>
                ) {
                    @Serializable
                    data class Link(
                        val icon: ApiImage,
                        val navigationEndpoint: NavigationEndpoint,
                        val title: SimpleText
                    ) {
                        @Serializable
                        data class NavigationEndpoint(val urlEndpoint: UrlEndpoint) {
                            @Serializable
                            data class UrlEndpoint(val url: String)
                        }
                    }
                }
            }
        }
    }

    @Serializable
    class Content
}

@Serializable
internal class ApiChannelContinuation(
    override val onResponseReceivedActions: List<ContinuationContents<ApiChannel.Content>>
) : ApiBrowseContinuation()