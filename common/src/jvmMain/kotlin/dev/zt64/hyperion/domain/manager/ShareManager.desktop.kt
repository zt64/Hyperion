package dev.zt64.hyperion.domain.manager

import org.koin.core.component.KoinComponent

internal actual class ShareManager : KoinComponent {
    actual fun share(
        content: String,
        label: String?
    ) {
        // NO-OP on desktop
    }
}