package com.zt.innertube.network.body

import com.zt.innertube.network.dto.ApiContext
import kotlinx.serialization.Serializable

@Serializable
internal data class AccountsBody(
    override val context: ApiContext
) : Body {
    val accountReadMask = AccountReadMask(returnOwner = true)

    @Serializable
    data class AccountReadMask(val returnOwner: Boolean)
}