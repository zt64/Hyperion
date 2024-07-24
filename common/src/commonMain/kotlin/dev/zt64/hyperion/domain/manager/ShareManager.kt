package dev.zt64.hyperion.domain.manager

import org.koin.core.component.KoinComponent

internal expect class ShareManager() : KoinComponent {
    /**
     * @param content
     * @param label
     */
    fun share(
        content: String,
        label: String? = null
    )
}