package com.zt.innertube.domain.model

import kotlin.time.Duration

data class DomainChapter(
    val title: String,
    val thumbnail: String,
    val start: Duration
)