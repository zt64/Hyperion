package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.model.SettingsScreenModel
import dev.zt64.hyperion.ui.navigation.SettingsSection
import dev.zt64.hyperion.ui.screen.settings.AboutScreen
import dev.zt64.hyperion.ui.screen.settings.AccountsScreen
import dev.zt64.hyperion.ui.screen.settings.AppearanceScreen
import dev.zt64.hyperion.ui.screen.settings.BackupRestoreScreen
import dev.zt64.hyperion.ui.screen.settings.DearrowScreen
import dev.zt64.hyperion.ui.screen.settings.GeneralScreen
import dev.zt64.hyperion.ui.screen.settings.NotificationsScreen
import dev.zt64.hyperion.ui.screen.settings.SponsorBlockScreen

object SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val model: SettingsScreenModel = koinScreenModel()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val snackbarHostState = remember { SnackbarHostState() }
        val navigator = LocalNavigator.currentOrThrow

        Navigator(BaseSettingsScreen) { settingsNavigator ->
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    AdaptiveTopBar(
                        navigationIcon = {
                            BackButton(
                                onClick = {
                                    if (settingsNavigator.size > 1) {
                                        settingsNavigator.pop()
                                    } else {
                                        navigator.pop()
                                    }
                                }
                            )
                        },
                        title = {
                            Text(
                                stringResource(
                                    when (settingsNavigator.lastItem) {
                                        GeneralScreen -> SettingsSection.GENERAL
                                        AboutScreen -> SettingsSection.ABOUT
                                        AccountsScreen -> SettingsSection.ACCOUNTS
                                        AppearanceScreen -> SettingsSection.APPEARANCE
                                        BackupRestoreScreen -> SettingsSection.BACKUP_RESTORE
                                        NotificationsScreen -> SettingsSection.NOTIFICATIONS
                                        SponsorBlockScreen -> SettingsSection.SPONSOR_BLOCK
                                        DearrowScreen -> SettingsSection.DEARROW
                                        else -> SettingsSection
                                    }.label
                                )
                            )
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { paddingValues ->
                SlideTransition(settingsNavigator) {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(scrollState),
                        content = { it.Content() }
                    )
                }
            }
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
                            navigator.push(
                                when (destination) {
                                    SettingsSection.ABOUT -> AboutScreen
                                    SettingsSection.ACCOUNTS -> AccountsScreen
                                    SettingsSection.APPEARANCE -> AppearanceScreen
                                    SettingsSection.BACKUP_RESTORE -> BackupRestoreScreen
                                    SettingsSection.GENERAL -> GeneralScreen
                                    SettingsSection.NOTIFICATIONS -> NotificationsScreen
                                    SettingsSection.SPONSOR_BLOCK -> SponsorBlockScreen
                                    SettingsSection.DEARROW -> DearrowScreen
                                    SettingsSection.GESTURES -> TODO()
                                }
                            )
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