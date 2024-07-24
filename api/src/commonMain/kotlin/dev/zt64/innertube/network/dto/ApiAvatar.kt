package dev.zt64.innertube.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject

@Serializable
internal data class ApiAvatar(val endpoint: OnTap<ApiNavigationEndpoint>, val image: ApiImage)
internal typealias DecoratedAvatar =
    @Serializable(DecoratedAvatarSerializer::class)
    ApiImage

internal object DecoratedAvatarSerializer : JsonTransformingSerializer<ApiImage>(
    ApiImage.serializer()
) {
    override fun transformDeserialize(element: JsonElement) = element
        .jsonObject["avatar"]!!
        .jsonObject["image"]!!
}