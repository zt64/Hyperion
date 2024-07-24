package dev.zt64.innertube.network.dto.browse

import dev.zt64.innertube.network.dto.Continuation
import kotlinx.serialization.json.JsonElement

// @Serializable
// internal class ApiTrending(override val contents: Contents<JsonElement>) : ApiBrowse()

internal typealias ApiTrending = Browse<JsonElement>

internal typealias ApiTrendingContinuation = Continuation<JsonElement>