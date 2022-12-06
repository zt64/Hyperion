package com.zt.innertube.network.dto.renderer

import kotlinx.serialization.Serializable

@Serializable
internal data class ChipCloudRenderer<T>(val chips: List<T> = emptyList())