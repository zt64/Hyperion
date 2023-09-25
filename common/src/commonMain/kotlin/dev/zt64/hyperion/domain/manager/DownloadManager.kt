package dev.zt64.hyperion.domain.manager

// class DownloadManager(
//     application: Application,
//     preferencesManager: PreferencesManager
// ) {
//     private val downloadManager = application.getSystemService<DownloadManager>()
//     private val downloadDirectory = preferencesManager.downloadDirectory.toUri()
// }

expect class DownloadManager(preferencesManager: PreferencesManager) {
    fun download()
}