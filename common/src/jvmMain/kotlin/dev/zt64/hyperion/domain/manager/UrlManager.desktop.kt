package dev.zt64.hyperion.domain.manager

import org.koin.core.component.KoinComponent
import java.awt.Desktop
import java.net.URI

internal actual class UrlManager : KoinComponent {
    actual fun openUrl(url: String) {
        Desktop.getDesktop().browse(URI(url))
    }
}