package dev.zt64.innertube.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
internal data class ApiImage(
    @JsonNames("thumbnails")
    val sources: List<ApiImageSource>
)

@Serializable
data class ApiImageSource(
    val url: String,
    val width: Int = 0,
    val height: Int = 0
)

internal typealias ImageContainer = @Serializable(ImageContainerSerializer::class) ApiImage

private object ImageContainerSerializer : JsonTransformingSerializer<ApiImage>(ApiImage.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonObject["image"] ?: element.jsonObject["elementsImage"]!!
    }
}