package com.hyperion.ui.screen

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.navigation.HomeDestination
import com.xinto.taxi.BackstackNavigator
import com.xinto.taxi.Taxi
import com.xinto.taxi.rememberNavigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainRootScreen(
    navigator: BackstackNavigator<AppDestination>
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec = rememberSplineBasedDecay(),
        state = rememberTopAppBarScrollState()
    )
    val mainRootNavigator = rememberNavigator(HomeDestination.HOME)
    val currentDestination = mainRootNavigator.currentDestination
    val orientation = LocalConfiguration.current.orientation

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(mainRootNavigator.currentDestination.label)) },
                actions = {
                    IconButton(onClick = { navigator.push(AppDestination.Search) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                    IconButton(onClick = { navigator.push(AppDestination.Settings) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_screen)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                NavigationBar {

                    HomeDestination.values().forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination == destination,
                            icon = { Icon(destination.icon, stringResource(destination.label)) },
                            label = { Text(stringResource(destination.label)) },
                            onClick = { mainRootNavigator.replace(destination) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier.padding(paddingValues)
        ) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                NavRail(
                    currentDestination = currentDestination,
                    onClickDestination = mainRootNavigator::replace
                )
            }

            Taxi(
                modifier = Modifier.weight(1f, true),
                navigator = mainRootNavigator,
                transitionSpec = { fadeIn() with fadeOut() }
            ) { destination ->
                when (destination) {
                    HomeDestination.HOME -> HomeScreen(
                        onClickVideo = { videoId -> navigator.push(AppDestination.Player(videoId)) },
                        onClickChannel = { channelId -> navigator.push(AppDestination.Channel(channelId)) },
                    )
                    HomeDestination.FEED -> FeedScreen()
                    HomeDestination.LIBRARY -> LibraryScreen()
                }
            }
        }

    }
}

@Composable
fun NavRail(
    currentDestination: HomeDestination,
    onClickDestination: (HomeDestination) -> Unit
) = NavigationRail {
    HomeDestination.values().forEach { destination ->
        NavigationRailItem(
            selected = currentDestination == destination,
            icon = { Icon(destination.icon, stringResource(destination.label)) },
            label = { Text(stringResource(destination.label)) },
            onClick = { onClickDestination(destination) }
        )
    }
}