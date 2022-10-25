package com.hyperion.network.body

import com.hyperion.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
data class BrowseBody(
    override val context: ApiContext,
    val browseId: String,
    val continuation: String? = null,
    val params: String? = null
) : Body