package com.hyperion.network.body

import com.hyperion.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchBody(
    override val context: ApiContext,
    val query: String,
    val params: String? = null,
    val continuation: String? = null
) : Body