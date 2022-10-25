package com.hyperion.domain.model

import androidx.compose.ui.graphics.Color

data class DomainTag(
    val name: String,
    val subtitle: String,
    val avatars: List<String>,
    val content: Content
) {
    data class Content(
        val videos: List<DomainVideoPartial>,
        val continuation: String? = null
    )
}

data class DomainTagPartial(
    val name: String,
    val videosCount: String,
    val channelsCount: String,
    val backgroundColor: Color
) : Entity