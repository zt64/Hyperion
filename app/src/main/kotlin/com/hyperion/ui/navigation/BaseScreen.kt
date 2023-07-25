package com.hyperion.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.ui.LocalNavController
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun BaseScreen(
    windowSizeClass: WindowSizeClass,
    hideNavLabel: Boolean,
    content: @Composable (BaseDestination) -> Unit
) {
    val rootNavController = LocalNavController.current
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
                        val interactionSource = remember { MutableInteractionSource() }

                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .size(40.dp)
                                .clickable(
                                    onClick = {
                                        rootNavController.navigate(AppDestination.Notifications)
                                    },
                                    role = Role.Button,
                                    interactionSource = interactionSource,
                                    indication = rememberRipple(
                                        bounded = false,
                                        radius = 20.dp
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            BadgedBox(
                                badge = {
//                                    Badge {
//                                        Text("57")
//                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsNone,
                                    contentDescription = stringResource(R.string.notifications)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                rootNavController.navigate(AppDestination.Search)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }

                        IconButton(
                            onClick = {
                                rootNavController.navigate(AppDestination.Settings)
                            }
                        ) {
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
                        BaseDestination.entries.forEach { destination ->
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
                        BaseDestination.entries.forEach { destination ->
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