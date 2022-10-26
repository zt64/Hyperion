package com.hyperion.network.dto.renderer

import kotlinx.serialization.Serializable

@Serializable
data class ListRenderer<T>(val items: List<T> = emptyList())