package dev.zt64.hyperion.api.network.body

import dev.zt64.hyperion.api.network.dto.InnerTubeContext

internal interface IBody {
    val context: InnerTubeContext
}