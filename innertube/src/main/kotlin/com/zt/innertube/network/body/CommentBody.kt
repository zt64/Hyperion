package com.zt.innertube.network.body

import com.zt.innertube.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
internal data class CommentBody(
    override val context: ApiContext,
    val commentText: String,
    val createCommentParams: String
) : Body