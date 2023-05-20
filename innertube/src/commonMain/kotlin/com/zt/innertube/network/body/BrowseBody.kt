package com.zt.innertube.network.body

import com.zt.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class BrowseBody(
    override val context: InnerTubeContext,
    val browseId: String,
    val continuation: String? = null,
    val params: String? = null
) : IBody