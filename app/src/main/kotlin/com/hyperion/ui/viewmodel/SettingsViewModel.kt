package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.hyperion.preferences.Prefs
import com.hyperion.ui.theme.Theme
import com.hyperion.util.githubUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    var showThemePicker by mutableStateOf(false)
        private set

    fun showThemePicker() {
        showThemePicker = true
    }

    fun dismissThemePicker() {
        showThemePicker = false
    }

    fun setTheme(theme: Theme) {
        Prefs.theme = theme
    }

    fun openGitHub() {
        val intent = Intent(Intent.ACTION_VIEW, githubUrl.toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        application.startActivity(intent)
    }
}