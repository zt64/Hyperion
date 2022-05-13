package com.hyperion.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ApiSearch(
    val contents: JsonObject,
    val estimatedResults: Int
)