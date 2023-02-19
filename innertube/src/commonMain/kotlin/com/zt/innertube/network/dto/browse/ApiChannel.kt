package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.*
import com.zt.innertube.network.dto.renderer.ElementRenderer
import com.zt.innertube.network.dto.renderer.ListRenderer
import com.zt.innertube.network.dto.renderer.ShelfRenderer
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
    data class Header(val channelMobileHeaderRenderer: ChannelMobileHeaderRenderer) {
        @Serializable
        data class ChannelMobileHeaderRenderer(val channelHeader: ChannelHeader)

        @Serializable
        data class ChannelHeader(val elementRenderer: ElementRenderer<Model>)

        @Serializable
        data class Model(val channelHeaderModel: ChannelHeaderModel)
    }

    @Serializable
    data class Content(
        val shelfRenderer: ShelfRenderer<ShelfRendererContent>? = null
    )

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
            data class Model(val gridVideoModel: ApiVideo? = null)

            @Serializable
            data class GridChannelRenderer(
                val channelId: String,
                val navigationEndpoint: ApiNavigationEndpoint,
                val shortSubscriberCountText: ApiText? = null,
                val subscriberCountText: ApiText? = null,
                val thumbnail: ApiImage,
                val title: ApiText
            )
        }

        @Serializable
        data class VerticalListItem(val elementRenderer: ElementRenderer<Model>) {
            @Serializable
            data class Model(val videoWithContextModel: ApiNextVideo? = null)
        }
    }

    @Serializable
    data class ChannelHeaderModel(
        val channelBanner: ImageContainer? = null,
        val channelProfile: ChannelProfile
    ) {
        @Serializable
        data class ChannelProfile(
            val avatarData: AvatarData,
            val descriptionPreview: DescriptionPreview,
            val metadata: Metadata,
            val title: String
        ) {
            @Serializable
            data class AvatarData(val avatar: ImageContainer)

            @Serializable
            data class DescriptionPreview(val description: String? = null)

            @Serializable
            data class Metadata(
                val joinDateText: String,
                val subscriberCountText: String? = null,
                val videosCountText: String? = null
            )
        }
    }
}

@Serializable
internal class ApiChannelContinuation(
    override val continuationContents: ContinuationContents<ApiChannel.Content>
) : ApiBrowseContinuation()