package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.hyperion.domain.manager.PreferencesManager

@Stable
class SettingsViewModel(
    private val application: Application,
    val preferences: PreferencesManager
) : ViewModel() {
    fun checkForUpdates() {

    }

    fun setDownloadUri(uri: Uri?) {
        if (uri != null) preferences.downloadDirectory = uri.toString()
    }

    fun openGitHub() {
        val intent = Intent(Intent.ACTION_VIEW, REPO_URL.toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        application.startActivity(intent)
    }

    private companion object {
        const val REPO_URL = "https://github.com/zt64/Hyperion"
    }
}