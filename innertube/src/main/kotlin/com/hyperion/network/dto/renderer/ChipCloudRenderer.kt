package com.hyperion.network.dto.renderer

import kotlinx.serialization.Serializable

@Serializable
data class ChipCloudRenderer<T>(val chips: List<T> = emptyList())