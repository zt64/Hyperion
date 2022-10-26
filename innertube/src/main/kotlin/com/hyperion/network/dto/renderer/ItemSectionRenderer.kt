package com.hyperion.network.dto.renderer

import kotlinx.serialization.Serializable

@Serializable
internal data class ItemSectionRenderer<T>(val contents: List<T>)