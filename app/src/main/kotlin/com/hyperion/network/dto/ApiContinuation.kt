package com.hyperion.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = ApiContinuation.Serializer::class)
sealed interface ApiContinuation {
    val continuation: String

    companion object Serializer : JsonContentPolymorphicSerializer<ApiContinuation>(ApiContinuation::class) {
        override fun selectDeserializer(element: JsonElement) = when {
            "nextContinuationData" in element.jsonObject -> Next.serializer()
            "reloadContinuationData" in element.jsonObject -> Reload.serializer()
            else -> throw NoWhenBranchMatchedException()
        }
    }

    @Serializable
    data class Next(private val nextContinuationData: NextContinuationData) : ApiContinuation {
        override val continuation: String = nextContinuationData.continuation

        @Serializable
        data class NextContinuationData(override val continuation: String) : ApiContinuation
    }

    @Serializable
    data class Reload(private val reloadContinuationData: ReloadContinuationData) : ApiContinuation {
        override val continuation: String = reloadContinuationData.continuation

        @Serializable
        data class ReloadContinuationData(override val continuation: String) : ApiContinuation
    }
}