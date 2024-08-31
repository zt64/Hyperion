package dev.zt64.hyperion.api.network.body

import dev.zt64.hyperion.api.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class CommentBody(
    override val context: InnerTubeContext,
    val commentText: String,
    val createCommentParams: String
) : IBody