package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.component.AdaptiveTopAppBarDefaults
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import dev.zt64.hyperion.ui.component.PlainTooltipBox
import dev.zt64.hyperion.ui.screen.base.FeedTab
import dev.zt64.hyperion.ui.screen.base.HomeTab
import dev.zt64.hyperion.ui.screen.base.LibraryTab
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

object BaseScreen : Screen {
    // @OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
    @Composable
    override fun Content() {
        val scrollBehavior = AdaptiveTopAppBarDefaults.adaptiveScrollBehavior()
        val navigator = LocalNavigator.currentOrThrow
        val drawerState = rememberDrawerState(DrawerValue.Closed)

        TabNavigator(HomeTab) { tabNavigator ->
            val preferences: PreferencesManager = koinInject()
            val tabNavigator = LocalTabNavigator.current

            // NavigationSuiteScaffold(
            //     navigationSuiteItems = {
            //         item(
            //             selected = tabNavigator.current == HomeTab,
            //             alwaysShowLabel = !preferences.hideNavItemLabel,
            //             icon = {
            //                 Icon(
            //                     imageVector = Icons.Default.Home,
            //                     contentDescription = stringResource(MR.strings.home)
            //                 )
            //             },
            //             label = { Text(stringResource(MR.strings.home)) },
            //             onClick = { tabNavigator.current = HomeTab }
            //         )
            //
            //         item(
            //             selected = tabNavigator.current == FeedTab,
            //             alwaysShowLabel = !preferences.hideNavItemLabel,
            //             icon = {
            //                 Icon(
            //                     imageVector = Icons.Default.DynamicFeed,
            //                     contentDescription = stringResource(MR.strings.feed)
            //                 )
            //             },
            //             label = { Text(stringResource(MR.strings.feed)) },
            //             onClick = { tabNavigator.current = FeedTab }
            //         )
            //
            //         item(
            //             selected = tabNavigator.current == LibraryTab,
            //             alwaysShowLabel = !preferences.hideNavItemLabel,
            //             icon = {
            //                 Icon(
            //                     imageVector = Icons.Default.VideoLibrary,
            //                     contentDescription = stringResource(MR.strings.library)
            //                 )
            //             },
            //             label = { Text(stringResource(MR.strings.library)) },
            //             onClick = { tabNavigator.current = LibraryTab }
            //         )
            //     },
            //     layoutType = NavigationSuiteType.NavigationRail
            // ) {
            //     CompositionLocalProvider(LocalNavigator provides navigator) {
            //         CurrentTab()
            //     }
            // }

            HyperionScaffold(
                drawerState = drawerState,
                scrollBehavior = scrollBehavior,
                drawerContent = {
                    Drawer()
                },
                topBar = {
                    val coroutineScope = rememberCoroutineScope()

                    TopBar(
                        onClickDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onClickNotifications = {
                            navigator.push(NotificationsScreen)
                        },
                        onClickSearch = {
                            navigator.push(SearchScreen)
                        },
                        onClickSettings = {
                            navigator.push(SettingsScreen)
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(FeedTab)
                        TabNavigationItem(LibraryTab)
                    }
                },
                navigationRail = {
                    NavigationRail {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(FeedTab)
                        TabNavigationItem(LibraryTab)
                    }
                },
                content = {
                    // evil workaround but it works
                    CompositionLocalProvider(LocalNavigator provides navigator) {
                        CurrentTab()
                    }
                }
            )
        }
    }
}

@Composable
private fun HyperionScaffold(
    drawerState: DrawerState,
    scrollBehavior: TopAppBarScrollBehavior,
    drawerContent: @Composable ColumnScope.() -> Unit,
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    navigationRail: @Composable () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(content = drawerContent)
        }
    ) {
        val windowSizeClass = LocalWindowSizeClass.current

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = topBar,
            bottomBar = {
                if (
                    windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact &&
                    windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact
                ) {
                    bottomBar()
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) {
                    navigationRail()
                }

                content()
            }
        }
    }
}

/**
 * TODO: Figure out way to display additional navigation icons on desktop, and Android tv
 *
 * @param onClickDrawer
 * @param onClickNotifications
 * @param onClickSearch
 * @param onClickSettings
 * @param scrollBehavior
 */
@Composable
private fun TopBar(
    onClickDrawer: () -> Unit,
    onClickNotifications: () -> Unit,
    onClickSearch: () -> Unit,
    onClickSettings: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val accountManager = koinInject<AccountManager>()

    AdaptiveTopBar(
        title = {
            val navigator = LocalTabNavigator.current

            Text(navigator.current.options.title)
        },
        navigationIcon = {
            IconButton(onClickDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
                )
            }
        },
        actions = {
            val interactionSource = remember { MutableInteractionSource() }

            if (accountManager.loggedIn) {
                PlainTooltipBox(
                    tooltip = {
                        PlainTooltip {
                            Text(stringResource(MR.strings.notifications))
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(40.dp)
                            .clickable(
                                onClick = onClickNotifications,
                                role = Role.Button,
                                interactionSource = interactionSource,
                                indication = LocalIndication.current
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // TODO: Fetch notification count from API
                        val notificationCount = remember { 0 }

                        BadgedBox(
                            badge = {
                                if (notificationCount > 0) {
                                    Badge {
                                        Text(
                                            text = if (notificationCount > 99) {
                                                "99+"
                                            } else {
                                                notificationCount.toString()
                                            }
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = stringResource(MR.strings.notifications)
                            )
                        }
                    }
                }
            }

            PlainTooltipBox(
                tooltip = {
                    PlainTooltip {
                        Text(stringResource(MR.strings.search))
                    }
                }
            ) {
                IconButton(onClickSearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(MR.strings.search)
                    )
                }
            }

            PlainTooltipBox(
                tooltip = {
                    PlainTooltip {
                        Text(stringResource(MR.strings.settings))
                    }
                }
            ) {
                IconButton(onClickSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(MR.strings.settings)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun Drawer() {
    Text(
        modifier = Modifier.padding(16.dp),
        text = stringResource(MR.strings.app_name),
        style = MaterialTheme.typography.titleLarge
    )

    HorizontalDivider()

    NavigationDrawerItem(
        selected = false,
        label = {
            Text("Explore")
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = "Explore"
            )
        },
        onClick = {
        }
    )
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val preferences: PreferencesManager = koinInject()
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(
        selected = tabNavigator.current == tab,
        icon = { Icon(tab.options.icon!!, null) },
        label = if (!preferences.hideNavItemLabel) {
            { Text(tab.options.title) }
        } else {
            null
        },
        onClick = { tabNavigator.current = tab }
    )
}

@Composable
private fun ColumnScope.TabNavigationItem(tab: Tab) {
    val preferences: PreferencesManager = koinInject()
    val tabNavigator = LocalTabNavigator.current

    NavigationRailItem(
        selected = tabNavigator.current == tab,
        icon = { Icon(tab.options.icon!!, null) },
        label = if (!preferences.hideNavItemLabel) {
            { Text(tab.options.title) }
        } else {
            null
        },
        onClick = { tabNavigator.current = tab }
    )
}