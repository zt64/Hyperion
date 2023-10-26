package dev.zt64.hyperion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dev.zt64.hyperion.Hyperion
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.ui.theme.HyperionTheme
import dev.zt64.hyperion.ui.theme.Theme
import dev.zt64.innertube.network.service.InnerTubeService
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val preferences: PreferencesManager = koinInject()

            HyperionTheme(
                isDarkTheme = preferences.theme == Theme.SYSTEM && isSystemInDarkTheme() || preferences.theme == Theme.DARK,
                isDynamicColor = preferences.dynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val innerTubeService: InnerTubeService = koinInject()
                    val state by innerTubeService.state.collectAsState()
                    val isReady by remember { derivedStateOf { state is InnerTubeService.State.Initialized } }

                    if (!isReady) {
                        Box(
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Hyperion()
                    }
                }
            }
        }
    }
}