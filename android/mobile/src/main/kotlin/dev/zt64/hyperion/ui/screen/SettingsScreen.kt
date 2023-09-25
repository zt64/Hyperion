package dev.zt64.hyperion.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.olshevski.navigation.reimagined.*
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.navigation.LocalNavController
import dev.zt64.hyperion.ui.navigation.SettingsDestination
import dev.zt64.hyperion.ui.navigation.SettingsSection
import dev.zt64.hyperion.ui.navigation.util.currentDestination
import dev.zt64.hyperion.ui.screen.settings.*
import dev.zt64.hyperion.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinViewModel()
    val parentNavController = LocalNavController.current
    val navController = rememberNavController<SettingsDestination>(SettingsSection)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                navigationIcon = {
                    BackButton(
                        onClick = {
                            if (navController.currentDestination == SettingsSection) {
                                parentNavController
                            } else {
                                navController
                            }.pop()
                        }
                    )
                },
                title = {
                    Crossfade(
                        targetState = navController.currentDestination,
                        label = "Title"
                    ) { destination ->
                        Text(stringResource(if (destination is SettingsSection) destination.label else MR.strings.settings))
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

        AnimatedNavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            controller = navController,
            transitionSpec = { action, _, _ ->
                if (action == NavAction.Pop) {
                    // Leaving settings sub-screen
                    fadeIn(tween(200)) togetherWith slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = FastOutLinearInEasing
                        )
                    )
                } else {
                    // Entering settings sub-screen
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    ) togetherWith fadeOut(tween())
                }
            }
        ) { destination ->
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val preferences = viewModel.preferences
                val coroutineScope = rememberCoroutineScope()

                when (destination) {
                    SettingsSection -> {
                        SettingsSection.entries.forEach { destination ->
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

                    SettingsSection.GENERAL -> GeneralScreen(
                        preferences = preferences,
                        onClickDownload = viewModel::setDownloadUri
                    )

                    SettingsSection.APPEARANCE -> AppearanceScreen(preferences)
                    SettingsSection.GESTURES -> GesturesScreen(preferences)
                    SettingsSection.NOTIFICATIONS -> NotificationsScreen(preferences)
                    SettingsSection.ACCOUNTS -> AccountsScreen(
                        onClickAddAccount = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Adding accounts not yet implemented!")
                            }
                        },
                    )

                    SettingsSection.SPONSOR_BLOCK -> SponsorBlockScreen(
                        preferences = preferences,
                        onClickCategory = { category ->

                        }
                    )

                    SettingsSection.BACKUP_RESTORE -> BackupRestoreScreen(preferences)
                    SettingsSection.ABOUT -> AboutScreen(
                        onClickUpdate = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Updating not yet implemented!")
                            }
                        },
                        onClickGithub = viewModel::openGitHub,
                    )
                }
            }
        }
    }
}