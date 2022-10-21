package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class OnTap<T>(val innertubeCommand: T)