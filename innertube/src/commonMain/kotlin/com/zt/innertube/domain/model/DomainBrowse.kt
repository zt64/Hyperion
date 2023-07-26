package com.zt.innertube.domain.model

import androidx.compose.runtime.Immutable

@Immutable
open class DomainBrowse<T>(
    open val items: List<T> = emptyList(),
    open val continuation: String? = null
)