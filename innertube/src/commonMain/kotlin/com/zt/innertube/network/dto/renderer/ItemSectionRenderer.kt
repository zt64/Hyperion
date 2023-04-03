package com.zt.innertube.network.dto.renderer

import kotlinx.serialization.Serializable

@Serializable
internal open class ItemSectionRenderer<T>(open val contents: List<T> = emptyList())