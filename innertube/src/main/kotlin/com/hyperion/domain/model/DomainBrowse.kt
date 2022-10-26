package com.hyperion.domain.model

import kotlinx.serialization.Serializable

@Serializable
open class DomainBrowse<T>(
    open val items: List<T> = emptyList(),
    open val continuation: String? = null
)