package com.hyperion.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthorThumbnail(
    val width: String,
    val height: String,
    val url: String,
) : Parcelable