package dev.zt64.innertube.network.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

internal typealias Continuation<T> = @Serializable(ContinuationSerializer::class) List<T>

internal class ContinuationSerializer<T : Any>(tSerializer: KSerializer<T>) :
    JsonTransformingSerializer<List<T>>(ListSerializer(tSerializer)) {
    override fun transformDeserialize(element: JsonElement) = element
        .jsonObject["onResponseReceivedActions"]!!
        .jsonArray.single()
        .jsonObject["appendContinuationItemsAction"]!!
        .jsonObject["continuationItems"]!!
}