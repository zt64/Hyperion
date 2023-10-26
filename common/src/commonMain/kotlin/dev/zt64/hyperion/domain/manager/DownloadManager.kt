package dev.zt64.hyperion.domain.manager

import org.koin.core.component.KoinComponent

// class DownloadManager(
//     application: Application,
//     preferencesManager: PreferencesManager
// ) {
//     private val downloadManager = application.getSystemService<DownloadManager>()
//     private val downloadDirectory = preferencesManager.downloadDirectory.toUri()
// }

expect class DownloadManager(preferencesManager: PreferencesManager) : KoinComponent {
    fun download()
}