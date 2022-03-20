package com.hyperion.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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
import com.hyperion.ui.screens.destinations.HomeScreenDestination
import com.hyperion.ui.screens.destinations.LibraryScreenDestination
import com.hyperion.ui.screens.destinations.SubscriptionsScreenDestination
import com.hyperion.ui.screens.navDestination
import com.ramcosta.composedestinations.navigation.navigateTo
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

private enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Home(HomeScreenDestination, Icons.Default.Home, R.string.home_screen),
    Subscriptions(SubscriptionsScreenDestination, Icons.Default.List, R.string.subscriptions_screen),
    Library(LibraryScreenDestination, Icons.Default.VideoLibrary, R.string.library_screen),
}

@Composable
fun BottomBar(
    navController: NavController
) {
    NavigationBar {
        val currentDestination = navController.currentBackStackEntryAsState().value?.navDestination

        BottomBarDestination.values().forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination.direction,
                icon = { Icon(destination.icon, stringResource(destination.label)) },
                label = { Text(stringResource(destination.label)) },
                onClick = {
                    navController.navigateTo(destination.direction) {
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}