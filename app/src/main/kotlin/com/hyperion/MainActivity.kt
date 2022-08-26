package com.hyperion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.screen.*
import com.hyperion.ui.theme.HyperionTheme
import com.hyperion.ui.theme.Theme
import com.xinto.taxi.Destination
import com.xinto.taxi.Navigator
import com.xinto.taxi.rememberBackstackNavigator
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val preferences: PreferencesManager by inject()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            HyperionTheme(
                isDarkTheme = preferences.theme == Theme.SYSTEM && isSystemInDarkTheme() || preferences.theme == Theme.DARK,
                isDynamicColor = preferences.dynamicColor
            ) {
                val navigator = rememberBackstackNavigator<AppDestination>(AppDestination.Home)

                BackHandler {
                    if (!navigator.pop()) finish()
                }

                Surface {
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
                                onClickBack = navigator::pop
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T : Destination> Taxi(
    navigator: Navigator<T>,
    transitionSpec: AnimatedContentScope<T>.() -> ContentTransform,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    val saveableStateHolder = rememberSaveableStateHolder()

    AnimatedContent(
        modifier = modifier,
        targetState = navigator.currentDestination,
        transitionSpec = transitionSpec,
        contentAlignment = Alignment.Center
    ) { currentDestination ->
        saveableStateHolder.SaveableStateProvider(currentDestination) {
            content(currentDestination)
        }
    }
}