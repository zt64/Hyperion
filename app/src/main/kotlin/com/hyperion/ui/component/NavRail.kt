package com.hyperion.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hyperion.R
import com.hyperion.ui.screen.appDestination
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun NavRail(
    navController: NavController
) = NavigationRail(
    header = { Icon(painterResource(R.drawable.ic_launcher_foreground), null) },
    content = {
        val currentDestination = navController.currentBackStackEntryAsState().value?.appDestination()

        NavigationDestination.values().forEach { destination ->
            NavigationRailItem(
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
)