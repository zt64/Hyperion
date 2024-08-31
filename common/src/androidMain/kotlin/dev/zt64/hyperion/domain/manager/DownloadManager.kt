package dev.zt64.hyperion.domain.manager

import android.app.Application
import androidx.core.net.toUri
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal actual class DownloadManager actual constructor(preferencesManager: PreferencesManager) : KoinComponent {
    private val application: Application = get()

    // private val downloadManager = application.getSystemService<DownloadManager>()
    private val downloadDirectory = preferencesManager.downloadDirectory.toUri()

    actual fun download() {
    }
}