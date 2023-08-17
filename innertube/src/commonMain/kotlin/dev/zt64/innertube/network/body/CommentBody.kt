package dev.zt64.innertube.network.body

import dev.zt64.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class CommentBody(
    override val context: InnerTubeContext,
    val commentText: String,
    val createCommentParams: String
) : IBody