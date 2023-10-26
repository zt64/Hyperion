package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.*
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.domain.manager.AccountManager
import dev.zt64.hyperion.domain.manager.PreferencesManager
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import dev.zt64.hyperion.ui.screen.base.FeedTab
import dev.zt64.hyperion.ui.screen.base.HomeTab
import dev.zt64.hyperion.ui.screen.base.LibraryTab
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

object BaseScreen : Screen {
    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val navigator = LocalNavigator.currentOrThrow
        val drawerState = rememberDrawerState(DrawerValue.Closed)

        TabNavigator(HomeTab) {
            HyperionScaffold(
                drawerState = drawerState,
                scrollBehavior = scrollBehavior,
                drawerContent = {
                    Text(
                        text = stringResource(MR.strings.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
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
            },
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
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .size(40.dp)
                        .clickable(
                            onClick = onClickNotifications,
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
                            // Badge {
                            //     Text("57")
                            // }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = stringResource(MR.strings.notifications)
                        )
                    }
                }
            }

            IconButton(onClickSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(MR.strings.search)
                )
            }

            IconButton(onClickSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(MR.strings.settings)
                )
            }
        },
        scrollBehavior = scrollBehavior
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
        } else null,
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
        } else null,
        onClick = { tabNavigator.current = tab }
    )
}