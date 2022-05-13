package com.hyperion.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hyperion.R
import com.hyperion.ui.screens.appDestination
import com.hyperion.ui.screens.destinations.HomeScreenDestination
import com.hyperion.ui.screens.destinations.LibraryScreenDestination
import com.hyperion.ui.screens.destinations.SubscriptionsScreenDestination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class NavigationDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Home(HomeScreenDestination, Icons.Default.Home, R.string.home),
    Subscriptions(SubscriptionsScreenDestination, Icons.Default.Subscriptions, R.string.subscriptions_screen),
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
            is SubscriptionsScreenDestination,
            is LibraryScreenDestination -> true
            else -> false
        },
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        NavigationBar {
            NavigationDestination.values().forEach { destination ->
                NavigationBarItem(
                    selected = currentDestination == destination.direction,
                    icon = { Icon(destination.icon, stringResource(destination.label)) },
                    label = { Text(stringResource(destination.label)) },
                    onClick = {
                        navController.navigate(destination.direction) {
                            popUpTo(navController.graph.startDestinationId) {
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