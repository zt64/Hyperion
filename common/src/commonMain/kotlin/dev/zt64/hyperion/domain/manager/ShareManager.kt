package dev.zt64.hyperion.domain.manager

import org.koin.core.component.KoinComponent

expect class ShareManager() : KoinComponent {
    fun share(content: String, label: String? = null)
}