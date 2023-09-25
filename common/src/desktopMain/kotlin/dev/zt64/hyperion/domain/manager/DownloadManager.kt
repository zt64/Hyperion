package dev.zt64.hyperion.domain.manager

actual class DownloadManager actual constructor(preferencesManager: PreferencesManager) {
    private val downloadDirectory = preferencesManager.downloadDirectory

    actual fun download() {

    }
}