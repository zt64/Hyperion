package com.hyperion.network.body

import com.hyperion.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
internal data class NextBody(
    override val context: ApiContext,
    val videoId: String,
    val continuation: String? = null
) : Body