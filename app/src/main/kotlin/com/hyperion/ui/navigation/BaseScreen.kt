package com.hyperion.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import dev.olshevski.navigation.reimagined.*
import kotlinx.coroutines.launch

private fun <T> NavController<T>.switchTo(destination: T) {
    if (!moveToTop { it == destination }) navigate(destination)
}

@Composable
fun BaseScreen(
    windowSizeClass: WindowSizeClass,
    hideNavLabel: Boolean,
    onClickSearch: () -> Unit,
    onClickSettings: () -> Unit,
    content: @Composable (BaseDestination) -> Unit
) {
    val navController = rememberNavController(BaseDestination.HOME)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    title = {
                        Text(stringResource(navController.currentDestination.label))
                    },
                    navigationIcon = {
                        val coroutineScope = rememberCoroutineScope()

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null
                            )
                        }
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
                    windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact &&
                    windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact
                ) {
                    NavigationBar {
                        BaseDestination.values().forEach { destination ->
                            key(destination) {
                                NavigationBarItem(
                                    selected = navController.currentDestination == destination,
                                    icon = { Icon(destination.icon, null) },
                                    label = if (!hideNavLabel) {
                                        { Text(stringResource(destination.label)) }
                                    } else null,
                                    onClick = { navController.switchTo(destination) }
                                )
                            }
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
                        BaseDestination.values().forEach { destination ->
                            NavigationRailItem(
                                selected = navController.currentDestination == destination,
                                icon = { Icon(destination.icon, null) },
                                label = if (!hideNavLabel) {
                                    { Text(stringResource(destination.label)) }
                                } else null,
                                onClick = { navController.switchTo(destination) }
                            )
                        }
                    }
                }

                AnimatedNavHost(
                    modifier = Modifier
                        .weight(1f, true)
                        .fillMaxHeight(),
                    controller = navController
                ) { destination ->
                    content(destination)
                }
            }
        }
    }
}