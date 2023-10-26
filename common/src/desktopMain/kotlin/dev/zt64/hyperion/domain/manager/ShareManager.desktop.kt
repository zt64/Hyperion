package dev.zt64.hyperion.domain.manager

import org.koin.core.component.KoinComponent

actual class ShareManager : KoinComponent {
    actual fun share(content: String, label: String?) = Unit
}