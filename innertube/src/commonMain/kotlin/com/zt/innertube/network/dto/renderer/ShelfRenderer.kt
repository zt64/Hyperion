package com.zt.innertube.network.dto.renderer

import kotlinx.serialization.Serializable

@Serializable
internal data class ShelfRenderer<T>(val content: T)