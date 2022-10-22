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
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.navigation.Taxi
import com.hyperion.ui.navigation.rememberBackstackNavigator
import com.hyperion.ui.screen.*
import com.hyperion.ui.theme.HyperionTheme
import com.hyperion.ui.theme.Theme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val preferences: PreferencesManager by inject()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            HyperionTheme(
                isDarkTheme = preferences.theme == Theme.SYSTEM && isSystemInDarkTheme() || preferences.theme == Theme.DARK,
                isDynamicColor = preferences.dynamicColor
            ) {
                Surface {
                    val navigator = rememberBackstackNavigator<AppDestination>(AppDestination.Root)

                    BackHandler {
                        if (!navigator.pop()) finish()
                    }

                    Taxi(
                        modifier = Modifier.fillMaxSize(),
                        navigator = navigator,
                        transitionSpec = {
                            fadeIn() with fadeOut()
                        }
                    ) { destination ->
                        fun onClickVideo(id: String) = navigator.push(AppDestination.Player(id))
                        fun onClickChannel(id: String) = navigator.push(AppDestination.Channel(id))

                        when (destination) {
                            AppDestination.Root -> {
                                RootScreen(
                                    onClickSearch = { navigator.push(AppDestination.Search) },
                                    onClickSettings = { navigator.push(AppDestination.Settings) },
                                    onClickVideo = ::onClickVideo,
                                    onClickChannel = ::onClickChannel
                                )
                            }

                            AppDestination.Search -> {
                                SearchScreen(
                                    navigator = navigator,
                                    onClickBack = navigator::pop,
                                    onClickChannel = ::onClickChannel,
                                    onClickPlaylist = { id ->
                                        navigator.push(AppDestination.Playlist(id))
                                    }
                                )
                            }

                            is AppDestination.Player -> {
                                PlayerScreen(
                                    navigator = navigator,
                                    videoId = destination.videoId
                                )
                            }

                            is AppDestination.Channel -> {
                                ChannelScreen(
                                    navigator = navigator,
                                    channelId = destination.channelId,
                                    onClickBack = navigator::pop
                                )
                            }

                            is AppDestination.Playlist -> {
                                PlaylistScreen(
                                    playlistId = destination.playlistId,
                                    onClickVideo = ::onClickVideo,
                                    onClickBack = navigator::pop
                                )
                            }

                            AppDestination.Settings -> {
                                SettingsScreen(
                                    onClickBack = navigator::pop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}