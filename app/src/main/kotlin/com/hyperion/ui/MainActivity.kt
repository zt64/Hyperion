package com.hyperion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.navigation.BaseDestination
import com.hyperion.ui.navigation.BaseScreen
import com.hyperion.ui.screen.*
import com.hyperion.ui.screen.base.FeedScreen
import com.hyperion.ui.screen.base.HomeScreen
import com.hyperion.ui.screen.base.LibraryScreen
import com.hyperion.ui.theme.HyperionTheme
import com.hyperion.ui.theme.Theme
import dev.olshevski.navigation.reimagined.*
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val preferences: PreferencesManager by inject()

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            HyperionTheme(
                isDarkTheme = preferences.theme == Theme.SYSTEM && isSystemInDarkTheme() || preferences.theme == Theme.DARK,
                isDynamicColor = preferences.dynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController<AppDestination>(BaseDestination.HOME)

                    NavBackHandler(navController)

                    AnimatedNavHost(
                        controller = navController
                    ) { destination ->
                        fun onClickVideo(id: String) = navController.navigate(AppDestination.Player(id))
                        fun onClickChannel(id: String) = navController.navigate(AppDestination.Channel(id))

                        when (destination) {
                            is BaseDestination -> {
                                BaseScreen(
                                    windowSizeClass = windowSizeClass,
                                    hideNavLabel = preferences.hideNavItemLabel,
                                    onClickSearch = { navController.navigate(AppDestination.Search) },
                                    onClickSettings = { navController.navigate(AppDestination.Settings) }
                                ) { baseDestination ->
                                    when (baseDestination) {
                                        BaseDestination.HOME -> {
                                            HomeScreen(
                                                onClickVideo = ::onClickVideo,
                                                onClickChannel = ::onClickChannel
                                            )
                                        }

                                        BaseDestination.FEED -> FeedScreen()
                                        BaseDestination.LIBRARY -> LibraryScreen()
                                    }
                                }
                            }

                            AppDestination.Search -> {
                                SearchScreen(
                                    navController = navController,
                                    onClickBack = navController::pop,
                                    onClickChannel = ::onClickChannel,
                                    onClickPlaylist = { id ->
                                        navController.navigate(AppDestination.Playlist(id))
                                    },
                                    onClickTag = { name ->
                                        navController.navigate(AppDestination.Tag(name))
                                    }
                                )
                            }

                            is AppDestination.Player -> {
                                PlayerScreen(
                                    navController = navController,
                                    videoId = destination.videoId
                                )
                            }

                            is AppDestination.Channel -> {
                                ChannelScreen(
                                    navController = navController,
                                    channelId = destination.channelId,
                                    onClickBack = navController::pop
                                )
                            }

                            is AppDestination.Playlist -> {
                                PlaylistScreen(
                                    playlistId = destination.playlistId,
                                    onClickVideo = ::onClickVideo,
                                    onClickBack = navController::pop
                                )
                            }

                            is AppDestination.Tag -> {
                                TagScreen(
                                    tag = destination.tag,
                                    onClickVideo = ::onClickVideo,
                                    onClickBack = navController::pop
                                )
                            }

                            AppDestination.AddAccount -> {}

                            AppDestination.Settings -> {
                                SettingsScreen(
                                    onClickBack = navController::pop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}