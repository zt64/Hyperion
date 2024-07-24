package dev.zt64.innertube.network.body

import dev.zt64.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class BrowseBody(
    override val context: InnerTubeContext,
    val browseId: String,
    val continuation: String? = null,
    val params: String? = null
) : IBody