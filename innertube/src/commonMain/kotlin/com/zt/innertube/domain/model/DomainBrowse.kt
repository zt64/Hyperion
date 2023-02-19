package com.zt.innertube.domain.model

open class DomainBrowse<T>(
    open val items: List<T> = emptyList(),
    open val continuation: String? = null
)