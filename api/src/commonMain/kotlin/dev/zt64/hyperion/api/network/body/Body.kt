package dev.zt64.hyperion.api.network.body

import dev.zt64.hyperion.api.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal class Body(override val context: InnerTubeContext) : IBody