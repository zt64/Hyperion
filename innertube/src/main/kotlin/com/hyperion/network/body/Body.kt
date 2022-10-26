package com.hyperion.network.body

import com.hyperion.network.dto.ApiContext

internal interface Body {
    val context: ApiContext
}