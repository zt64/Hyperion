package com.hyperion.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseOutExpo
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.ui.navigation.SettingsDestination
import com.hyperion.ui.navigation.Taxi
import com.hyperion.ui.navigation.rememberNavigator
import com.hyperion.ui.screen.settings.*
import com.hyperion.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel(),
    onClickBack: () -> Unit
) {
    val navigator = rememberNavigator(SettingsDestination.MAIN)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (navigator.currentDestination != SettingsDestination.MAIN) {
                                navigator.replace(SettingsDestination.MAIN)
                            } else {
                                onClickBack()
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
                    Crossfade(navigator.currentDestination) { destination ->
                        Text(stringResource(destination.label))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        BackHandler(
            enabled = navigator.currentDestination != SettingsDestination.MAIN
        ) {
            navigator.replace(SettingsDestination.MAIN)
        }

        Taxi(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            navigator = navigator,
            transitionSpec = {
                if (targetState == SettingsDestination.MAIN) {
                    fadeIn() with slideOutHorizontally(
                        animationSpec = tween(200, easing = EaseInExpo),
                        targetOffsetX = { width -> width }
                    ) + fadeOut()
                } else {
                    slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutExpo),
                        initialOffsetX = { width -> width }
                    ) + fadeIn(tween(400)) with fadeOut()
                } using SizeTransform(clip = false)
            }
        ) { destination ->
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (destination) {
                        SettingsDestination.MAIN -> {
                            SettingsDestination.values()
                                .toMutableList().apply {
                                    remove(SettingsDestination.MAIN)
                                }
                                .forEach { destination ->
                                    ListItem(
                                        modifier = Modifier.clickable {
                                            navigator.replace(destination)
                                        },
                                        headlineText = {
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

                        SettingsDestination.GENERAL -> GeneralScreen(viewModel)
                        SettingsDestination.APPEARANCE -> AppearanceScreen(viewModel)
                        SettingsDestination.ACCOUNTS -> AccountsScreen(viewModel)
                        SettingsDestination.SPONSOR_BLOCK -> SponsorBlockScreen(viewModel)
                        SettingsDestination.BACKUP_RESTORE -> BackupRestoreScreen(viewModel)
                        SettingsDestination.ABOUT -> AboutScreen(viewModel)
                    }
                }
            }
        }
    }
}