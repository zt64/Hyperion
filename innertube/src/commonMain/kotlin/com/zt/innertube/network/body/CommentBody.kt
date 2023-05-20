package com.zt.innertube.network.body

import com.zt.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class CommentBody(
    override val context: InnerTubeContext,
    val commentText: String,
    val createCommentParams: String
) : IBody