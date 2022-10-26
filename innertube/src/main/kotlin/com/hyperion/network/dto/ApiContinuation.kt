package com.hyperion.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

internal val List<ApiContinuation>.next: String?
    get() = singleOrNull { it is ApiContinuation.Next }?.continuation

@Serializable(with = ApiContinuation.Serializer::class)
sealed interface ApiContinuation {
    val continuation: String

    companion object Serializer : JsonContentPolymorphicSerializer<ApiContinuation>(ApiContinuation::class) {
        override fun selectDeserializer(element: JsonElement) = when (element.jsonObject.keys.single()) {
            "nextContinuationData" -> Next.serializer()
            "reloadContinuationData" -> Reload.serializer()
            else -> throw NoWhenBranchMatchedException()
        }
    }

    @Serializable
    data class ContinuationData(val continuation: String)

    @Serializable
    data class Next(private val nextContinuationData: ContinuationData) : ApiContinuation {
        override val continuation: String = nextContinuationData.continuation
    }

    @Serializable
    data class Reload(private val reloadContinuationData: ContinuationData) : ApiContinuation {
        override val continuation: String = reloadContinuationData.continuation
    }
}