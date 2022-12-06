package com.zt.innertube.network.dto.renderer

import com.zt.innertube.network.dto.ApiContinuation
import kotlinx.serialization.Serializable

@Serializable
internal data class SectionListRenderer<T>(
    val contents: List<T> = emptyList(),
    val continuations: List<ApiContinuation> = emptyList()
)