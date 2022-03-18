package com.hyperion.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrendingVideo(
    val author: String,
    val authorId: String,
    val authorUrl: String,
    val description: String,
    val descriptionHtml: String,
    val lengthSeconds: Int,
    val liveNow: Boolean,
    val paid: Boolean,
    val premium: Boolean,
    val published: Int,
    val publishedText: String,
    val title: String,
    val videoId: String,
    val videoThumbnails: List<VideoThumbnail>,
    val viewCount: Int
) : Parcelable