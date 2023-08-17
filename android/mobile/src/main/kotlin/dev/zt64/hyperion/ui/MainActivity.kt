package dev.zt64.hyperion.ui

import android.app.Activity
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.zt64.hyperion.LocalNavController
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.ui.navigation.*
import dev.zt64.hyperion.ui.screen.*
import dev.zt64.hyperion.ui.screen.base.FeedScreen
import dev.zt64.hyperion.ui.screen.base.HomeScreen
import dev.zt64.hyperion.ui.screen.base.LibraryScreen
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

                CompositionLocalProvider(
                    LocalNavController provides navController
                ) {
                    AnimatedNavHost(
                        modifier = Modifier.fillMaxSize(),
                        controller = navController
                    ) { destination ->
                        when (destination) {
                            is BaseDestination -> BaseScreen(
                                windowSizeClass = windowSizeClass,
                                hideNavLabel = preferences.hideNavItemLabel
                            ) { baseDestination ->
                                when (baseDestination) {
                                    BaseDestination.HOME -> HomeScreen()
                                    BaseDestination.FEED -> FeedScreen()
                                    BaseDestination.LIBRARY -> LibraryScreen()
                                }
                            }

                            AppDestination.Search -> SearchScreen()

                            is AppDestination.Player -> PlayerScreen(
                                videoId = destination.videoId
                            )

                            is AppDestination.Channel -> ChannelScreen(
                                channelId = destination.channelId
                            )

                            is AppDestination.Playlist -> PlaylistScreen(
                                playlistId = destination.playlistId
                            )

                            is AppDestination.Tag -> TagScreen(
                                tag = destination.tag
                            )

                            AppDestination.AddAccount -> AddAccountScreen()
                            AppDestination.Notifications -> NotificationsScreen()
                            AppDestination.Channels -> ChannelsScreen()
                            AppDestination.History -> HistoryScreen()
                            AppDestination.Settings -> SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}