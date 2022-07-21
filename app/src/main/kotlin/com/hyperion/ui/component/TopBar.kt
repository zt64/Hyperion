package com.hyperion.ui.component

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
import com.hyperion.R
import com.hyperion.ui.screen.appDestination
import com.hyperion.ui.screen.destinations.*
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
        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
    ) {
        SmallTopAppBar(
            navigationIcon = {
                if (
                    when (currentDestination) {
                        HomeScreenDestination,
                        FeedScreenDestination,
                        LibraryScreenDestination -> false
                        else -> true
                    }
                ) {
                    IconButton(onClick = navController::popBackStack) {
                        Icon(
                            imageVector = Icons.Default.NavigateBefore,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            },
            title = {
                currentDestination.title?.let { id -> Text(stringResource(id)) }
            },
            actions = {
                if (currentDestination !is SettingsScreenDestination) {
                    if (currentDestination !is SearchScreenDestination) {
                        IconButton(
                            onClick = { navController.navigate(SearchScreenDestination) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    }

                    IconButton(
                        onClick = { navController.navigate(SettingsScreenDestination) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_screen)
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
    }
}