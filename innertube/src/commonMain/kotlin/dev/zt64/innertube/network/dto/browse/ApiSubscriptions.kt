package dev.zt64.innertube.network.dto.browse

import dev.zt64.innertube.network.dto.Continuation
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class ApiSubscriptions(override val contents: Contents<JsonElement>) : ApiBrowse()

internal typealias ApiSubscriptionsContinuation = Continuation<JsonElement>