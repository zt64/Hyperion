package com.hyperion

import android.content.Context
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hyperion.network.service.InnerTubeService
import com.hyperion.preferences.Prefs
import com.hyperion.preferences.sharedPreferences
import com.hyperion.ui.components.HyperionScaffold
import com.hyperion.ui.theme.HyperionTheme
import com.hyperion.ui.theme.Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : ComponentActivity() {
    @Inject
    lateinit var innerTubeService: InnerTubeService

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setOnExitAnimationListener { provider ->
            provider.view.animate().apply {
                interpolator = AccelerateInterpolator()
                duration = 200L

                alpha(0f)
                withEndAction(provider::remove)
                start()
            }
        }

        super.onCreate(savedInstanceState)

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        runBlocking { innerTubeService.initApi() }

        setContent {
            HyperionTheme(
                isDarkTheme = Prefs.theme == Theme.SYSTEM && isSystemInDarkTheme() || Prefs.theme == Theme.DARK
            ) {
                HyperionScaffold()
            }
        }
    }
}