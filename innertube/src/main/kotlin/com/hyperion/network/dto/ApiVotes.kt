package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiVotes(
    val likes: Int,
    val dislikes: Int
)