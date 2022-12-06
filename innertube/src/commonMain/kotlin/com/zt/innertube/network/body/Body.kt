package com.zt.innertube.network.body

import com.zt.innertube.network.dto.ApiContext

internal interface Body {
    val context: ApiContext
}