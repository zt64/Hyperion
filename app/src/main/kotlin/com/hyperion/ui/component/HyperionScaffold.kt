package com.hyperion.ui.component

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.hyperion.R
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.screen.NavGraphs
import com.hyperion.ui.screen.appDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.navigate
import org.koin.androidx.compose.get

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class,
)
@Composable
fun HyperionScaffold(
    prefs: PreferencesManager = get()
) {
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
                startRoute = prefs.startScreen.direction,
                navGraph = NavGraphs.root,
                engine = navHostEngine
            )
        }
    }
}