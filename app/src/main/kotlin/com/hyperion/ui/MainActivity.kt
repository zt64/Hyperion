package com.hyperion.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.navigation.*
import com.hyperion.ui.screen.*
import com.hyperion.ui.screen.base.FeedScreen
import com.hyperion.ui.screen.base.HomeScreen
import com.hyperion.ui.screen.base.LibraryScreen
import com.hyperion.ui.theme.HyperionTheme
import com.hyperion.ui.theme.Theme
import com.zt.innertube.network.service.InnerTubeService
import dev.olshevski.navigation.reimagined.*
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Content(activity = this)
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun Content(
    activity: Activity,
    preferences: PreferencesManager = koinInject(),
    innerTubeService: InnerTubeService = koinInject()
) {
    val windowSizeClass = calculateWindowSizeClass(activity)

    HyperionTheme(
        isDarkTheme = preferences.theme == Theme.SYSTEM && isSystemInDarkTheme() || preferences.theme == Theme.DARK,
        isDynamicColor = preferences.dynamicColor
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val state by innerTubeService.state.collectAsState()

            if (state == InnerTubeService.State.Initializing) {
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val navController = rememberNavController<Destination>(BaseDestination.HOME)

                NavBackHandler(navController)

                AnimatedNavHost(
                    modifier = Modifier.fillMaxSize(),
                    controller = navController
                ) { destination ->
                    fun onClickVideo(id: String) = navController.navigate(AppDestination.Player(id))
                    fun onClickChannel(id: String) = navController.navigate(AppDestination.Channel(id))

                    when (destination) {
                        is BaseDestination -> {
                            BaseScreen(
                                windowSizeClass = windowSizeClass,
                                hideNavLabel = preferences.hideNavItemLabel,
                                onClickNotifications = { navController.navigate(AppDestination.Notifications) },
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

                                    BaseDestination.FEED -> FeedScreen(
                                        onClickSignIn = { },
                                    )

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

                        AppDestination.AddAccount -> AddAccountScreen(
                            onClickBack = navController::pop
                        )

                        AppDestination.Notifications -> NotificationsScreen(
                            onClickBack = navController::pop
                        )

                        AppDestination.Channels -> ChannelsScreen(
                            onClickBack = navController::pop
                        )

                        AppDestination.History -> HistoryScreen(
                            onClickBack = navController::pop
                        )

                        AppDestination.Settings -> SettingsScreen(
                            onClickBack = navController::pop
                        )
                    }
                }
            }
        }
    }
}