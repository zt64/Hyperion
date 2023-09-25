package dev.zt64.hyperion

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.ui.App
import dev.zt64.hyperion.ui.navigation.AppDestination
import dev.zt64.hyperion.ui.navigation.BaseDestination
import dev.zt64.hyperion.ui.navigation.Destination
import dev.zt64.hyperion.ui.theme.HyperionTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
suspend fun main() {
    application {
        App {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Hyperion",
                icon = null
            ) {
                HyperionTheme(true) {
                    Surface {
                        val scope = rememberCoroutineScope()
                        val drawerState = rememberDrawerState(DrawerValue.Closed)
                        var selectedDestination by remember {
                            mutableStateOf<Destination>(BaseDestination.HOME)
                        }

                        DismissibleNavigationDrawer(
                            drawerState = drawerState,
                            gesturesEnabled = false,
                            drawerContent = {
                                DismissibleDrawerSheet {
                                    Spacer(Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {

                                            }
                                            .padding(NavigationDrawerItemDefaults.ItemPadding),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .size(48.dp)
                                                .border(
                                                    3.dp,
                                                    MaterialTheme.colorScheme.onSurface,
                                                    CircleShape
                                                ),
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = null
                                        )

                                        Column {
                                            Text(
                                                text = "John Doe",
                                                style = MaterialTheme.typography.titleMedium
                                            )

                                            Spacer(Modifier.height(3.dp))

                                            Text(
                                                text = "Switch account",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }

                                    BaseDestination.entries.forEach { destination ->
                                        NavigationDrawerItem(
                                            modifier = Modifier.padding(
                                                NavigationDrawerItemDefaults.ItemPadding
                                            ),
                                            icon = {
                                                Icon(
                                                    imageVector = destination.icon,
                                                    contentDescription = null
                                                )
                                            },
                                            label = { Text(stringResource(destination.label)) },
                                            onClick = {
                                                selectedDestination = destination

                                                scope.launch { drawerState.close() }
                                            },
                                            selected = selectedDestination == destination
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    NavigationDrawerItem(
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = null
                                            )
                                        },
                                        label = { Text(stringResource(MR.strings.settings)) },
                                        onClick = {
                                            selectedDestination = AppDestination.Settings
                                            scope.launch { drawerState.close() }
                                        },
                                        selected = selectedDestination == AppDestination.Settings
                                    )

                                }
                            }
                        ) {
                            Scaffold(
                                topBar = {
                                    TopAppBar(
                                        navigationIcon = {
                                            IconButton(
                                                onClick = {
                                                    scope.launch { drawerState.open() }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Menu,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        title = {
                                            Text("Hyperion")
                                        }
                                    )
                                }
                            ) { paddingValues ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues)
                                ) {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}