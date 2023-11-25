package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.ui.LocalSnackbarHostState
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.model.SettingsScreenModel
import dev.zt64.hyperion.ui.navigation.SettingsSection
import dev.zt64.hyperion.ui.screen.settings.*
import dev.zt64.hyperion.ui.screen.settings.NotificationsScreen
import kotlinx.coroutines.launch

object SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val model: SettingsScreenModel = getScreenModel()
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
                                        AboutScreen -> SettingsSection.ABOUT
                                        AccountsScreen -> SettingsSection.ACCOUNTS
                                        AppearanceScreen -> SettingsSection.APPEARANCE
                                        BackupRestoreScreen -> SettingsSection.BACKUP_RESTORE
                                        GeneralScreen -> SettingsSection.GENERAL
                                        NotificationsScreen -> SettingsSection.NOTIFICATIONS
                                        SponsorBlockScreen -> SettingsSection.SPONSOR_BLOCK
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        it.Content()
                    }
                }
            }
        }
    }
}

object BaseSettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = LocalSnackbarHostState.currentOrThrow
        val scope = rememberCoroutineScope()

        SettingsSection.entries.forEach { destination ->
            key(destination.label) {
                ListItem(
                    modifier = Modifier
                        .clickable {
                            when (destination) {
                                SettingsSection.ABOUT -> navigator.push(AboutScreen)
                                SettingsSection.ACCOUNTS -> navigator.push(AccountsScreen)
                                SettingsSection.APPEARANCE -> navigator.push(AppearanceScreen)
                                SettingsSection.BACKUP_RESTORE -> navigator.push(BackupRestoreScreen)
                                SettingsSection.GENERAL -> navigator.push(GeneralScreen)
                                SettingsSection.NOTIFICATIONS -> navigator.push(NotificationsScreen)
                                SettingsSection.SPONSOR_BLOCK -> navigator.push(SponsorBlockScreen)
                                else -> scope.launch {
                                    snackbarHostState.showSnackbar("TODO")
                                }
                            }
                        }
                        .padding(vertical = 12.dp),
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
