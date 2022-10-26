package com.hyperion.network.body

import com.hyperion.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
data class PlayerBody(
    override val context: ApiContext,
    val videoId: String
) : Body