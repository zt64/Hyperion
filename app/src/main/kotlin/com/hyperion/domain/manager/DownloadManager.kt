package com.hyperion.domain.manager

import android.app.Application
import android.app.DownloadManager
import androidx.core.content.getSystemService
import androidx.core.net.toUri

class DownloadManager(
    application: Application,
    preferencesManager: PreferencesManager
) {
    private val downloadManager = application.getSystemService<DownloadManager>()
    private val downloadDirectory = preferencesManager.downloadDirectory.toUri()
}