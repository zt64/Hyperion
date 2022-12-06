package com.zt.innertube.network.body

import com.zt.innertube.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
internal data class NextBody(
    override val context: ApiContext,
    val videoId: String,
    val continuation: String? = null
) : Body