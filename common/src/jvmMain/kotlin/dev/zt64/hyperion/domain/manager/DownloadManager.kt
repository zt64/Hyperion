package dev.zt64.hyperion.domain.manager

import org.koin.core.component.KoinComponent
import java.io.File

internal actual class DownloadManager actual constructor(preferencesManager: PreferencesManager) : KoinComponent {
    private val downloadDirectory by preferencesManager::downloadDirectory

    actual fun download() {
        File(downloadDirectory)
    }
}