package dev.zt64.innertube.network.body

import dev.zt64.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class NextBody(
    override val context: InnerTubeContext,
    val videoId: String,
    val continuation: String? = null
) : IBody