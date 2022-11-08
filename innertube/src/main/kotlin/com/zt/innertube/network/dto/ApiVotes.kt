package com.zt.innertube.network.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ApiVotes(
    val likes: Int,
    val dislikes: Int
)