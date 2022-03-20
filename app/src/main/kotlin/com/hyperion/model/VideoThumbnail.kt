package com.hyperion.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoThumbnail(
    val width: String,
    val height: String,
    val quality: String,
    val url: String,
) : Parcelable