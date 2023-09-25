package dev.zt64.hyperion.domain.manager

import androidx.core.net.toUri

actual class DownloadManager actual constructor(preferencesManager: PreferencesManager) {
    // private val downloadManager = application.getSystemService<DownloadManager>()
    private val downloadDirectory = preferencesManager.downloadDirectory.toUri()
    
    actual fun download() {}
}