package dev.zt64.innertube.network.dto

import dev.zt64.innertube.network.dto.renderer.ItemSectionRendererSerializer
import dev.zt64.innertube.network.dto.renderer.Renderer
import dev.zt64.innertube.network.dto.renderer.UnknownRenderer
import dev.zt64.innertube.serializer.SingletonMapPolymorphicSerializer
import dev.zt64.innertube.serializer.TokenSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.protobuf.ProtoNumber

internal val searchModule = SerializersModule {
    polymorphicDefaultDeserializer(Renderer::class) {
        UnknownRenderer.serializer()
    }
}

@Serializable
internal class ApiSearchParams(
    @ProtoNumber(1) val sortBy: Int? = null,
    @ProtoNumber(2) val filters: Filters? = null,
    @ProtoNumber(19) val noFilter: Int? = null
) {
    @Serializable
    data class Filters(
        @ProtoNumber(1) val uploadDate: Int? = null,
        @ProtoNumber(2) val type: Int? = null,
        @ProtoNumber(3) val duration: Int? = null,
        @ProtoNumber(4) val featuresHd: Int? = null,
        @ProtoNumber(5) val featuresSubtitles: Int? = null,
        @ProtoNumber(6) val featuresCreativeCommons: Int? = null,
        @ProtoNumber(7) val features3d: Int? = null,
        @ProtoNumber(8) val featuresLive: Int? = null,
        @ProtoNumber(9) val featuresPurchased: Int? = null,
        @ProtoNumber(14) val features4k: Int? = null,
        @ProtoNumber(15) val features360: Int? = null,
        @ProtoNumber(23) val featuresLocation: Int? = null,
        @ProtoNumber(25) val featuresHdr: Int? = null,
        @ProtoNumber(26) val featuresVr180: Int? = null
    )
}

internal object ContentsSerializer :
    JsonTransformingSerializer<List<Item>>(ItemSectionRendererSerializer(Item.Serializer)) {
    override fun transformDeserialize(element: JsonElement): JsonElement = element
        .jsonObject["contents"]!!
        .jsonObject["twoColumnSearchResultsRenderer"]!!
        .jsonObject["primaryContents"]!!
        .jsonObject["sectionListRenderer"]!!
}

internal typealias ApiSearch = @Serializable(ContentsSerializer::class) List<Item>

@Serializable
internal sealed interface Item {
    object Serializer : SingletonMapPolymorphicSerializer<Item>(serializer())
}

@Serializable
@SerialName("itemSectionRenderer")
internal data class ItemSection(val contents: List<Renderer>) : Item

@Serializable
@SerialName("continuationItemRenderer")
internal data class ContinuationItem(
    @Serializable(TokenSerializer::class)
    @SerialName("continuationEndpoint")
    val token: String
) : Item

internal class ItemsSerializer :
    JsonTransformingSerializer<List<Item>>(ListSerializer(Item.Serializer)) {
    override fun transformDeserialize(element: JsonElement) = element
        .jsonArray.single()
        .jsonObject["appendContinuationItemsAction"]!!
        .jsonObject["continuationItems"]!!
}

@Serializable
internal data class ApiSearchContinuation(
    @Serializable(ItemsSerializer::class)
    @SerialName("onResponseReceivedCommands")
    val items: List<Item>
)