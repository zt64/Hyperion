package com.hyperion.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.ui.navigation.SettingsDestination
import com.hyperion.ui.navigation.SettingsSection
import com.hyperion.ui.navigation.currentDestination
import com.hyperion.ui.screen.settings.*
import com.hyperion.ui.viewmodel.SettingsViewModel
import dev.olshevski.navigation.reimagined.*
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel(),
    onClickBack: () -> Unit
) {
    val navController = rememberNavController<SettingsDestination>(SettingsSection)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (navController.currentDestination == SettingsSection) {
                                onClickBack()
                            } else {
                                navController.pop()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    Crossfade(
                        targetState = navController.currentDestination,
                        label = "Title"
                    ) { destination ->
                        Text(stringResource(if (destination is SettingsSection) destination.label else R.string.settings))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        NavBackHandler(navController)

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedNavHost(
                controller = navController,
                transitionSpec = { action, _, _ ->
                    if (action == NavAction.Pop) {
                        // Leaving settings sub-screen
                        fadeIn(tween(200)) with slideOutOfContainer(
                            towards = AnimatedContentScope.SlideDirection.End,
                            animationSpec = tween(
                                durationMillis = 200,
                                easing = FastOutLinearInEasing
                            )
                        )
                    } else {
                        // Entering settings sub-screen
                        slideIntoContainer(
                            towards = AnimatedContentScope.SlideDirection.Start,
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        ) with fadeOut(tween(300))
                    }
                }
            ) { destination ->
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (destination) {
                        SettingsSection -> {
                            SettingsSection.values().forEach { destination ->
                                ListItem(
                                    modifier = Modifier.clickable {
                                        navController.navigate(destination)
                                    },
                                    headlineContent = {
                                        Text(stringResource(destination.label))
                                    },
                                    leadingContent = {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = stringResource(destination.label)
                                        )
                                    }
                                )
                            }
                        }

                        SettingsSection.GENERAL -> GeneralScreen(viewModel)
                        SettingsSection.APPEARANCE -> AppearanceScreen(viewModel)
                        SettingsSection.ACCOUNTS -> AccountsScreen(viewModel)
                        SettingsSection.SPONSOR_BLOCK -> SponsorBlockScreen(viewModel)
                        SettingsSection.BACKUP_RESTORE -> BackupRestoreScreen(viewModel)
                        SettingsSection.ABOUT -> AboutScreen(viewModel, snackbarHostState)
                    }
                }
            }
        }
    }
}