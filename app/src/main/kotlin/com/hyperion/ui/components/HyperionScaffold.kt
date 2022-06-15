package com.hyperion.ui.components

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.hyperion.R
import com.hyperion.preferences.Prefs
import com.hyperion.ui.screens.NavGraphs
import com.hyperion.ui.screens.appDestination
import com.hyperion.ui.screens.destinations.IntroScreenDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.navigate

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class,
)
@Composable
fun HyperionScaffold() {
    val navController = rememberAnimatedNavController()
    val navHostEngine = rememberAnimatedNavHostEngine(
        rootDefaultAnimations = RootNavGraphDefaultAnimations(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ),
    )
    val orientation = LocalConfiguration.current.orientation
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarScrollState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = { if (orientation == Configuration.ORIENTATION_PORTRAIT) BottomBar(navController) }
    ) { paddingValues ->
        Row(
            modifier = Modifier.padding(paddingValues)
        ) {
            val startScreen by rememberUpdatedState(newValue = Prefs.startScreen)

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                NavigationRail(
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
            }

            DestinationsNavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startRoute = if (Prefs.firstLaunch) IntroScreenDestination else startScreen.direction,
                navGraph = if (Prefs.firstLaunch) NavGraphs.intro else NavGraphs.root,
                engine = navHostEngine
            )
        }
    }
}