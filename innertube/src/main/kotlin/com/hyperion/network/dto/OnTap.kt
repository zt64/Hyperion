package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class OnTap<T>(val innertubeCommand: T)