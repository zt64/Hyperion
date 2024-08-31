package dev.zt64.hyperion.api.network.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

internal typealias Continuation<T> = List<T>

internal class ContinuationSerializer<T : Any>(tSerializer: KSerializer<T>) :
    JsonTransformingSerializer<List<T>>(ListSerializer(tSerializer)) {
    override fun transformDeserialize(element: JsonElement) = element
        .jsonObject["onResponseReceivedActions"]!!
        .jsonArray
        .single()
        .jsonObject["appendContinuationItemsAction"]!!
        .jsonObject["continuationItems"]!!
}