package dev.zt64.hyperion.api.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class OnTap<T>(val innertubeCommand: T)