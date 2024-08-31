package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.AdaptiveTopAppBarDefaults
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.model.SettingsScreenModel
import dev.zt64.hyperion.ui.navigation.SettingsSection

object SettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class, InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val model: SettingsScreenModel = koinScreenModel()
        val scrollBehavior = AdaptiveTopAppBarDefaults.adaptiveScrollBehavior()
        val snackbarHostState = remember { SnackbarHostState() }
        val rootNavigator = LocalNavigator.currentOrThrow

        val navigator = rememberListDetailPaneScaffoldNavigator<SettingsSection>()

        BackHandler(navigator.canNavigateBack()) {
            navigator.navigateBack()
        }

        Scaffold(
            topBar = {
                AdaptiveTopBar(
                    navigationIcon = {
                        BackButton(
                            onClick = {
                                if (navigator.scaffoldValue.secondary ==
                                    PaneAdaptedValue.Expanded
                                ) {
                                    rootNavigator.pop()
                                } else {
                                    navigator.navigateBack()
                                }
                            }
                        )
                    },
                    title = {
                        Text(
                            text = if (navigator.scaffoldValue.secondary ==
                                PaneAdaptedValue.Expanded
                            ) {
                                null
                            } else {
                                navigator.currentDestination?.content?.label
                            }.let { stringResource(it ?: MR.strings.settings) }
                        )
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            ListDetailPaneScaffold(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(paddingValues),
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                listPane = {
                    AnimatedPane {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            SettingsSection.entries.forEach { destination ->
                                key(destination.label) {
                                    ListItem(
                                        modifier = Modifier
                                            .clickable {
                                                navigator.navigateTo(
                                                    ListDetailPaneScaffoldRole.Detail,
                                                    destination
                                                )
                                            }.padding(vertical = 12.dp),
                                        headlineContent = {
                                            Text(stringResource(destination.label))
                                        },
                                        leadingContent = {
                                            Icon(
                                                imageVector = destination.icon,
                                                contentDescription = stringResource(
                                                    destination.label
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                detailPane = {
                    AnimatedPane {
                        navigator.currentDestination?.content?.let {
                            val scrollState = rememberScrollState()

                            Column(
                                modifier = Modifier
                                    .verticalScroll(scrollState)
                            ) {
                                it.screen().Content()
                            }
                        }
                    }
                }
            )
        }
    }
}

object BaseSettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsSection.entries.forEach { destination ->
            key(destination.label) {
                ListItem(
                    modifier = Modifier
                        .clickable {
                            navigator.push(destination.screen())
                        }.padding(vertical = 12.dp),
                    headlineContent = { Text(stringResource(destination.label)) },
                    leadingContent = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = stringResource(destination.label)
                        )
                    }
                )
            }
        }
    }
}