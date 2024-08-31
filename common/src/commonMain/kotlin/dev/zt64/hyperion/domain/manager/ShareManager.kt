package dev.zt64.hyperion.domain.manager

import org.koin.core.component.KoinComponent

internal expect class ShareManager() : KoinComponent {
    /**
     * Share content using the platform's share feature
     *
     * @param content
     * @param label
     */
    fun share(content: String, label: String? = null)
}