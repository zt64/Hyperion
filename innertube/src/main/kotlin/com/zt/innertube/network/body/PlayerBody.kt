package com.zt.innertube.network.body

import com.zt.innertube.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
internal data class PlayerBody(
    override val context: ApiContext,
    val videoId: String
) : Body