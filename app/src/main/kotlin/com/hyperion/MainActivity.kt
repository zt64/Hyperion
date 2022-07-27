package com.hyperion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.screen.*
import com.hyperion.ui.theme.HyperionTheme
import com.hyperion.ui.theme.Theme
import com.xinto.taxi.Taxi
import com.xinto.taxi.rememberBackstackNavigator
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val prefs: PreferencesManager by inject()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            HyperionTheme(
                isBlack = prefs.midnightMode,
                isDarkTheme = prefs.theme == Theme.SYSTEM && isSystemInDarkTheme() || prefs.theme == Theme.DARK,
                isDynamicColor = prefs.dynamicColor
            ) {
                val navigator = rememberBackstackNavigator<AppDestination>(AppDestination.Home)

                BackHandler {
                    navigator.pop()
                }

                Taxi(
                    modifier = Modifier.fillMaxSize(),
                    navigator = navigator,
                    transitionSpec = { fadeIn() with fadeOut() }
                ) { destination ->
                    when (destination) {
                        is AppDestination.Home -> MainRootScreen(
                            navigator = navigator
                        )
                        is AppDestination.Search -> SearchScreen(
                            navigator = navigator
                        )
                        is AppDestination.Player -> PlayerScreen(
                            navigator = navigator,
                            videoId = destination.videoId
                        )
                        is AppDestination.Channel -> ChannelScreen(
                            navigator = navigator,
                            channelId = destination.channelId
                        )
                        is AppDestination.Settings -> SettingsScreen(
                            navigator = navigator
                        )
                    }
                }
            }
        }
    }
}