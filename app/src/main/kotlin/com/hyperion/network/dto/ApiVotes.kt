package com.hyperion.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiVotes(
    val dateCreated: String,
    val deleted: Boolean,
    val likes: Int,
    val dislikes: Int,
    val id: String,
    val rating: Double,
    val viewCount: Int
)