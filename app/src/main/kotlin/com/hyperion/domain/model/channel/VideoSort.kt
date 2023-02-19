package com.hyperion.domain.model.channel

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.hyperion.R

@Immutable
enum class VideoSort(@StringRes val displayName: Int) {
    RECENTLY_UPLOADED(R.string.recently_uploaded),
    POPULAR(R.string.popular)
}