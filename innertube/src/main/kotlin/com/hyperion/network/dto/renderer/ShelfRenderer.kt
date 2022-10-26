package com.hyperion.network.dto.renderer

import kotlinx.serialization.Serializable

@Serializable
data class ShelfRenderer<T>(val content: List<T>)