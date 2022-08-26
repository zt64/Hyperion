package com.hyperion.network.dto.renderer

import com.hyperion.network.dto.ApiContinuation
import kotlinx.serialization.Serializable

@Serializable
data class SectionListRenderer<T>(
    val contents: List<T> = emptyList(),
    val continuations: List<ApiContinuation> = emptyList()
)