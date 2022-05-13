package com.hyperion.network.body

import com.hyperion.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
data class SearchBody(
    val query: String,
    val params: String? = null,
    val context: ApiContext
)