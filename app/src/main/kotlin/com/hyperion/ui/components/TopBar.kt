package com.hyperion.ui.components

import androidx.compose.animation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hyperion.R
import com.hyperion.ui.screens.destinations.*
import com.hyperion.ui.screens.navDestination
import com.hyperion.util.title
import com.ramcosta.composedestinations.navigation.navigateTo

@Composable
fun TopBar(
    navController: NavController
) {
    val currentDestination =
        navController.currentBackStackEntryAsState().value?.navDestination ?: HomeScreenDestination

    AnimatedVisibility(
        visible = when (currentDestination) {
            is PlayerScreenDestination,
            is IntroScreenDestination -> false
            else -> true
        },
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
                        Icon(
                            imageVector = Icons.Default.NavigateBefore,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                }
            },
            title = {
                currentDestination.title?.let { Text(stringResource(id = it)) }
            },
            actions = {
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                }

                if (currentDestination != SettingsScreenDestination) {
                    IconButton(
                        onClick = { navController.navigateTo(SettingsScreenDestination) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_screen)
                        )
                    }
                }
            }
        )
    }
}