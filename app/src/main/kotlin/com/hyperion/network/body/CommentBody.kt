package com.hyperion.network.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentBody(
    @SerialName("text")
    val commentText: String
)