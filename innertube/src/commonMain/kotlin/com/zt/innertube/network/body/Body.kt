package com.zt.innertube.network.body

import com.zt.innertube.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface Body {
    val context: ApiContext
}