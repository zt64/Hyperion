package com.hyperion.network.body

import com.hyperion.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
internal data class PlayerBody(
    override val context: ApiContext,
    val videoId: String
) : Body