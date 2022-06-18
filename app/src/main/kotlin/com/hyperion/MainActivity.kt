package com.hyperion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.components.HyperionScaffold
import com.hyperion.ui.theme.HyperionTheme
import com.hyperion.ui.theme.Theme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val prefs: PreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            HyperionTheme(
                isBlack = prefs.midnightMode,
                isDarkTheme = prefs.theme == Theme.SYSTEM && isSystemInDarkTheme() || prefs.theme == Theme.DARK,
                isDynamicColor = prefs.materialYou
            ) {
                HyperionScaffold()
            }
        }
    }
}