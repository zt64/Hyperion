package com.zt.innertube.network.body

import com.zt.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchBody(
    override val context: InnerTubeContext,
    val query: String,
    val params: String? = null,
    val continuation: String? = null
) : IBody