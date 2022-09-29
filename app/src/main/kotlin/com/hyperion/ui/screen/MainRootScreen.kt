package com.hyperion.ui.screen

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.ui.navigation.AppDestination
import com.hyperion.ui.navigation.RootDestination
import com.xinto.taxi.BackstackNavigator
import com.xinto.taxi.Taxi
import com.xinto.taxi.rememberNavigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainRootScreen(
    navigator: BackstackNavigator<AppDestination>
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val mainRootNavigator = rememberNavigator(RootDestination.HOME)
    val currentDestination = mainRootNavigator.currentDestination
    val orientation = LocalConfiguration.current.orientation

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(currentDestination.label)) },
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
                    RootDestination.values().forEach { destination ->
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
                    RootDestination.HOME -> HomeScreen(
                        onClickVideo = { videoId -> navigator.push(AppDestination.Player(videoId)) },
                        onClickChannel = { channelId -> navigator.push(AppDestination.Channel(channelId)) },
                    )

                    RootDestination.FEED -> FeedScreen()
                    RootDestination.LIBRARY -> LibraryScreen()
                }
            }
        }
    }
}

@Composable
private fun NavRail(
    currentDestination: RootDestination,
    onClickDestination: (RootDestination) -> Unit
) {
    NavigationRail {
        RootDestination.values().forEach { destination ->
            NavigationRailItem(
                selected = currentDestination == destination,
                icon = { Icon(destination.icon, stringResource(destination.label)) },
                label = { Text(stringResource(destination.label)) },
                onClick = { onClickDestination(destination) }
            )
        }
    }
}