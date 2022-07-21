package com.hyperion.ui.component

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.hyperion.domain.manager.PreferencesManager
import com.hyperion.ui.screen.NavGraphs
import com.hyperion.ui.screen.appCurrentDestinationAsState
import com.hyperion.ui.screen.destinations.HomeScreenDestination
import com.hyperion.ui.screen.destinations.PlayerScreenDestination
import com.hyperion.ui.viewmodel.PlayerViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.navigate
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

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
    val orientation = LocalConfiguration.current.orientation
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarScrollState())

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = { if (orientation == Configuration.ORIENTATION_PORTRAIT) BottomBar(navController) }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) NavRail(navController)

            Column {
                val currentDestination = navController.appCurrentDestinationAsState().value ?: HomeScreenDestination
                val playerViewModel: PlayerViewModel = getViewModel()

                DestinationsNavHost(
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .weight(1f, true),
                    navController = navController,
                    startRoute = prefs.startScreen.direction,
                    navGraph = NavGraphs.root,
                    engine = rememberAnimatedNavHostEngine(
                        rootDefaultAnimations = RootNavGraphDefaultAnimations(
                            enterTransition = { fadeIn(tween()) },
                            exitTransition = { fadeOut(tween()) }
                        )
                    )
                )

                AnimatedVisibility(
                    visible = playerViewModel.player.currentMediaItem != null && currentDestination !is HomeScreenDestination,
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    MiniPlayer(
                        player = playerViewModel.player,
                        title = playerViewModel.video!!.title,
                        author = playerViewModel.video!!.author.name!!,
                        isPlaying = playerViewModel.isPlaying,
                        onClick = {
                            navController.navigate(PlayerScreenDestination())
                        },
                        onClickPlayPause = playerViewModel::togglePlayPause,
                        onClickClose = { /* TODO */ }
                    )
                }
            }
        }
    }
}