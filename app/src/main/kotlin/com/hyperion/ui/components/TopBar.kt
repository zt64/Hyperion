package com.hyperion.ui.components

import androidx.compose.animation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hyperion.ui.screens.appDestination
import com.hyperion.ui.screens.destinations.*
import com.hyperion.util.title
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun TopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.appDestination() ?: HomeScreenDestination

    AnimatedVisibility(
        visible = currentDestination !is PlayerScreenDestination,
        enter = expandVertically(expandFrom = Alignment.CenterVertically) + fadeIn(),
        exit = shrinkVertically(shrinkTowards = Alignment.CenterVertically) + fadeOut()
    ) {
        SmallTopAppBar(
            navigationIcon = {
                if (
                    when (currentDestination) {
                        HomeScreenDestination,
                        SubscriptionsScreenDestination,
                        LibraryScreenDestination -> false
                        else -> true
                    }
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(imageVector = Icons.Default.NavigateBefore, contentDescription = "Back")
                    }
                }
            },
            title = {
                currentDestination.title?.let { Text(stringResource(id = it)) }
            },
            actions = {
                if (currentDestination != SearchScreenDestination && currentDestination != SettingsScreenDestination) {
                    IconButton(
                        onClick = { navController.navigate(SearchScreenDestination) }
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }

                if (currentDestination != SettingsScreenDestination) {
                    IconButton(
                        onClick = { navController.navigate(SettingsScreenDestination) }
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
    }
}