package com.hyperion.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.hyperion.R
import com.hyperion.ui.navigation.RootDestination
import com.hyperion.ui.navigation.currentDestination
import com.hyperion.ui.viewmodel.FeedViewModel
import com.hyperion.ui.viewmodel.HomeViewModel
import com.hyperion.ui.viewmodel.LibraryViewModel
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceAll
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootScreen(
    windowSizeClass: WindowSizeClass,
    onClickSearch: () -> Unit,
    onClickSettings: () -> Unit,
    onClickVideo: (id: String) -> Unit,
    onClickChannel: (id: String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val navController = rememberNavController(RootDestination.HOME)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(stringResource(navController.currentDestination.label))
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
            if (
                windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact &&
                windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
            ) {
                NavigationBar {
                    RootDestination.values().forEach { destination ->
                        NavigationBarItem(
                            selected = navController.currentDestination == destination,
                            icon = { Icon(destination.icon, stringResource(destination.label)) },
                            label = { Text(stringResource(destination.label)) },
                            onClick = { navController.replaceAll(destination) }
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
            if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) {
                NavigationRail {
                    RootDestination.values().forEach { destination ->
                        NavigationRailItem(
                            selected = navController.currentDestination == destination,
                            icon = { Icon(destination.icon, stringResource(destination.label)) },
                            label = { Text(stringResource(destination.label)) },
                            onClick = { navController.replaceAll(destination) }
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f, true)
            ) {
                val homeViewModel = getViewModel<HomeViewModel>()
                val feedViewModel = getViewModel<FeedViewModel>()
                val libraryViewModel = getViewModel<LibraryViewModel>()

                AnimatedNavHost(
                    controller = navController
                ) { destination ->
                    when (destination) {
                        RootDestination.HOME -> {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onClickVideo = onClickVideo,
                                onClickChannel = onClickChannel
                            )
                        }

                        RootDestination.FEED -> FeedScreen(feedViewModel)
                        RootDestination.LIBRARY -> LibraryScreen(libraryViewModel)
                    }
                }
            }
        }
    }
}