package dev.zt64.innertube.network.body

import dev.zt64.innertube.network.dto.InnerTubeContext
import kotlinx.serialization.Serializable

@Serializable
internal data class AccountsBody(override val context: InnerTubeContext) : IBody {
    val accountReadMask = AccountReadMask(returnOwner = true)

    @Serializable
    data class AccountReadMask(val returnOwner: Boolean)
}