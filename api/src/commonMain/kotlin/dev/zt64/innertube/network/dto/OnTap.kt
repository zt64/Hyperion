package dev.zt64.innertube.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class OnTap<T>(val innertubeCommand: T)