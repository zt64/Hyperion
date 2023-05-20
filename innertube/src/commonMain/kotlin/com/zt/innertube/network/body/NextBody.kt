package com.zt.innertube.network.body

import com.zt.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class NextBody(
    override val context: InnerTubeContext,
    val videoId: String,
    val continuation: String? = null
) : IBody