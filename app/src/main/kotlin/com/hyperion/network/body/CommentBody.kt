package com.hyperion.network.body

import com.hyperion.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
data class CommentBody(
    override val context: ApiContext,
    val commentText: String,
    val createCommentParams: String
) : Body