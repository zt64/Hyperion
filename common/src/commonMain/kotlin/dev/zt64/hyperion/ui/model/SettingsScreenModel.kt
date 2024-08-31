package dev.zt64.hyperion.ui.model

import cafe.adriel.voyager.core.model.ScreenModel
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.domain.manager.UrlManager

internal class SettingsScreenModel(private val urlManager: UrlManager, val preferences: PreferencesManager) : ScreenModel {
    fun checkForUpdates() {
    }

    fun setDownloadUri(uri: String?) {
        // if (uri != null) preferences.downloadDirectory = uri.toString()
    }

    fun openGitHub() {
        urlManager.openUrl(REPO_URL)
    }

    fun backup() {
    }

    fun restore() {
    }

    private companion object {
        const val REPO_URL = "https://github.com/zt64/Hyperion"
    }
}