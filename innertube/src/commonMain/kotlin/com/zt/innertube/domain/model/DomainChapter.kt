package com.zt.innertube.domain.model

import androidx.compose.runtime.Immutable
import kotlin.time.Duration

@Immutable
data class DomainChapter(
    val title: String,
    val thumbnail: String,
    val start: Duration
)