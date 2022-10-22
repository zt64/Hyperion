package com.hyperion.ui.screen

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.hyperion.ui.navigation.RootDestination
import com.hyperion.ui.navigation.Taxi
import com.hyperion.ui.navigation.rememberNavigator

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootScreen(
    onClickSearch: () -> Unit,
    onClickSettings: () -> Unit,
    onClickVideo: (id: String) -> Unit,
    onClickChannel: (id: String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navigator = rememberNavigator(RootDestination.HOME)
    val orientation = LocalConfiguration.current.orientation

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(stringResource(navigator.currentDestination.label))
                },
                actions = {
                    IconButton(onClick = onClickSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                    IconButton(onClick = onClickSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
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
                            selected = navigator.currentDestination == destination,
                            icon = { Icon(destination.icon, stringResource(destination.label)) },
                            label = { Text(stringResource(destination.label)) },
                            onClick = {
                                navigator.replace(destination)
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                NavigationRail {
                    RootDestination.values().forEach { destination ->
                        NavigationRailItem(
                            selected = navigator.currentDestination == destination,
                            icon = { Icon(destination.icon, stringResource(destination.label)) },
                            label = { Text(stringResource(destination.label)) },
                            onClick = { navigator.replace(destination) }
                        )
                    }
                }
            }

            Taxi(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f, true),
                navigator = navigator,
                transitionSpec = {
                    fadeIn() with fadeOut()
                }
            ) { destination ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (destination) {
                        RootDestination.HOME -> {
                            HomeScreen(
                                onClickVideo = onClickVideo,
                                onClickChannel = onClickChannel
                            )
                        }

                        RootDestination.FEED -> {
                            FeedScreen()
                        }

                        RootDestination.LIBRARY -> {
                            LibraryScreen()
                        }
                    }
                }
            }
        }
    }
}