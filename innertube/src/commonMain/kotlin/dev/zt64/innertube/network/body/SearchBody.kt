package dev.zt64.innertube.network.body

import dev.zt64.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchBody(
    override val context: InnerTubeContext,
    val query: String,
    val params: String? = null,
    val continuation: String? = null
) : IBody