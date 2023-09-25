package dev.zt64.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import dev.zt64.hyperion.domain.manager.PreferencesManager

class SettingsViewModel(
    private val application: Application,
    val preferences: PreferencesManager
) : ViewModel() {
    fun checkForUpdates() {

    }

    fun setDownloadUri(uri: String?) {

        // if (uri != null) preferences.downloadDirectory = uri.toString()
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