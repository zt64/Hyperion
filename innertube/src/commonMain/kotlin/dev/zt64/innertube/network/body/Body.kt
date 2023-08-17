package dev.zt64.innertube.network.body

import dev.zt64.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal class Body(
    override val context: InnerTubeContext
) : IBody