package com.zt.innertube.network.dto.browse

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal class ApiTrending(
    override val contents: Contents<JsonElement>
) : ApiBrowse()

internal typealias ApiTrendingContinuation = ApiBrowseContinuation<JsonElement>