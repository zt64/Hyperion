package com.hyperion.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hyperion.R
import com.hyperion.ui.screen.appDestination
import com.hyperion.ui.screen.destinations.FeedScreenDestination
import com.hyperion.ui.screen.destinations.HomeScreenDestination
import com.hyperion.ui.screen.destinations.LibraryScreenDestination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class NavigationDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Home(HomeScreenDestination, Icons.Default.Home, R.string.home),
    Feed(FeedScreenDestination, Icons.Default.Subscriptions, R.string.feed),
    Library(LibraryScreenDestination, Icons.Default.VideoLibrary, R.string.library_screen),
}

@Composable
fun BottomBar(
    navController: NavController
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.appDestination()

    AnimatedVisibility(
        visible = when (currentDestination) {
            is HomeScreenDestination,
            is FeedScreenDestination,
            is LibraryScreenDestination -> true
            else -> false
        },
        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
    ) {
        NavigationBar {
            NavigationDestination.values().forEach { destination ->
                NavigationBarItem(
                    selected = currentDestination == destination.direction,
                    icon = { Icon(destination.icon, stringResource(destination.label)) },
                    label = { Text(stringResource(destination.label)) },
                    onClick = {
                        navController.navigate(destination.direction) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}