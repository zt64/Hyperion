package com.zt.innertube.network.dto.browse

import com.zt.innertube.network.dto.*
import com.zt.innertube.network.dto.renderer.ItemSectionRenderer
import com.zt.innertube.network.dto.renderer.SectionListRenderer
import com.zt.innertube.serializer.SingletonMapPolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
internal class PlaylistContinuation private constructor(
    @ProtoNumber(80226972)
    val continuation: Continuation
) {
    constructor(id: String, page: Int) : this(
        continuation = Continuation(
            playlistIdRaw = "VL$id",
            unknown = Continuation.Unknown(
                page = page,
                unknown = "PT:CGo"
            ),
            playlistId = id
        )
    )

    @Serializable
    data class Continuation(
        @ProtoNumber(2)
        val playlistId: String,
        @ProtoNumber(3)
        val unknown: Unknown,
        @ProtoNumber(35)
        val playlistIdRaw: String
    ) {
        @Serializable
        data class Unknown(
            @ProtoNumber(1)
            val page: Int,
            @ProtoNumber(15)
            val unknown: String
        )
    }
}

@Serializable
internal data class ApiPlaylist(
    val header: Header,
    override val contents: Contents<G>
) : ApiBrowse() {
    @Serializable
    data class Header(val playlistHeaderRenderer: PlaylistHeaderRenderer) {
        @Serializable
        data class PlaylistHeaderRenderer(
            val title: SimpleText,
            val ownerText: ApiText,
            val viewCountText: SimpleText,
            val ownerEndpoint: ApiNavigationEndpoint
        )
    }

    @Serializable
    data class G(val itemSectionRenderer: ItemSectionRenderer<SectionContent>)

    @Serializable
    data class SectionContent(
        val playlistVideoListRenderer: SectionListRenderer<@Serializable(Renderer.Serializer::class) Renderer>
    ) {
        @Serializable
        sealed interface Renderer {
            object Serializer : SingletonMapPolymorphicSerializer<Renderer>(serializer())
        }

        @Serializable
        @SerialName("playlistVideoRenderer")
        data class PlaylistVideo(
            val isPlayable: Boolean,
            val lengthText: SimpleText,
            val navigationEndpoint: NavigationEndpoint,
            val shortBylineText: ApiText,
            val thumbnail: ApiImage,
            val title: ApiText,
            val videoId: String,
            val videoInfo: ApiText
        ) : Renderer {
            @Serializable
            data class NavigationEndpoint(val watchEndpoint: ApiWatchEndpoint)
        }

        @Serializable
        @SerialName("continuationItemRenderer")
        data class ContinuationItem(
            @Serializable(TokenSerializer::class)
            @SerialName("continuationEndpoint")
            val token: String
        ) : Renderer {
            private class TokenSerializer : JsonTransformingSerializer<String>(String.serializer()) {
                override fun transformDeserialize(element: JsonElement): JsonElement {
                    return element.jsonObject["continuationCommand"]!!.jsonObject["token"]!!
                }
            }
        }
    }
}

@Serializable
internal data class ApiPlaylistContinuation(
    override val onResponseReceivedActions: List<ContinuationContents<@Serializable(ApiPlaylist.SectionContent.Renderer.Serializer::class) ApiPlaylist.SectionContent.Renderer>>
) : ApiBrowseContinuation()