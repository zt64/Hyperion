package dev.zt64.hyperion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.FadeTransition
import dev.zt64.hyperion.ui.LocalWindowSizeClass
import dev.zt64.hyperion.ui.ProvideSnackbarHostState
import dev.zt64.hyperion.ui.screen.BaseScreen
import dev.zt64.hyperion.ui.theme.HyperionTheme
import dev.zt64.innertube.network.service.InnerTubeService
import org.koin.compose.koinInject

@Composable
fun Hyperion() {
    HyperionTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val innerTubeService: InnerTubeService = koinInject()
            val state by innerTubeService.state.collectAsState()
            val isReady by remember {
                derivedStateOf { state is InnerTubeService.State.Initialized }
            }

            if (!isReady) {
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val windowSizeClass = LocalWindowSizeClass.current
                val snackbarHostState = remember { SnackbarHostState() }

                val app = remember {
                    movableContentOf {
                        Box {
                            Navigator(
                                screen = BaseScreen,
                                disposeBehavior = NavigatorDisposeBehavior(
                                    disposeNestedNavigators = false,
                                    disposeSteps = true
                                )
                            ) { navigator ->
                                FadeTransition(navigator)
                            }

                            SnackbarHost(
                                modifier = Modifier.align(Alignment.BottomCenter),
                                hostState = snackbarHostState
                            )
                        }
                    }
                }

                ProvideSnackbarHostState(snackbarHostState) {
                    app()
                    // if (windowSizeClass.widthSizeClass < WindowWidthSizeClass.Expanded) {
                    //     app()
                    // } else {
                    //     PermanentNavigationDrawer(
                    //         drawerContent = {
                    //             PermanentDrawerSheet {
                    //                 Spacer(Modifier.height(12.dp))
                    //
                    //                 Row(
                    //                     modifier = Modifier
                    //                         .fillMaxWidth()
                    //                         .clickable {
                    //
                    //                         }
                    //                         .padding(NavigationDrawerItemDefaults.ItemPadding),
                    //                     horizontalArrangement = Arrangement.spacedBy(8.dp),
                    //                     verticalAlignment = Alignment.CenterVertically
                    //                 ) {
                    //                     Icon(
                    //                         modifier = Modifier
                    //                             .clip(CircleShape)
                    //                             .size(48.dp)
                    //                             .border(
                    //                                 width = 3.dp,
                    //                                 color = MaterialTheme.colorScheme.onSurface,
                    //                                 shape = CircleShape
                    //                             ),
                    //                         imageVector = Icons.Default.AccountCircle,
                    //                         contentDescription = null
                    //                     )
                    //
                    //                     Column {
                    //                         Text(
                    //                             text = "John Doe",
                    //                             style = MaterialTheme.typography.titleMedium
                    //                         )
                    //
                    //                         Spacer(Modifier.height(3.dp))
                    //
                    //                         Text(
                    //                             text = "Switch account",
                    //                             style = MaterialTheme.typography.labelMedium
                    //                         )
                    //                     }
                    //                 }
                    //
                    //                 // BaseDestination.entries.forEach { destination ->
                    //                 //     NavigationDrawerItem(
                    //                 //         modifier = Modifier.padding(
                    //                 //             NavigationDrawerItemDefaults.ItemPadding
                    //                 //         ),
                    //                 //         icon = {
                    //                 //             Icon(
                    //                 //                 imageVector = destination.icon,
                    //                 //                 contentDescription = null
                    //                 //             )
                    //                 //         },
                    //                 //         label = { Text(stringResource(destination.label)) },
                    //                 //         onClick = {
                    //                 //             selectedDestination = destination
                    //                 //
                    //                 //             scope.launch { drawerState.close() }
                    //                 //         },
                    //                 //         selected = selectedDestination == destination
                    //                 //     )
                    //                 // }
                    //
                    //                 Spacer(modifier = Modifier.weight(1f))
                    //
                    //                 NavigationDrawerItem(
                    //                     modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    //                     icon = {
                    //                         Icon(
                    //                             imageVector = Icons.Default.Settings,
                    //                             contentDescription = null
                    //                         )
                    //                     },
                    //                     label = { Text(stringResource(MR.strings.settings)) },
                    //                     onClick = {
                    //                         // selectedDestination = AppDestination.Settings
                    //                     },
                    //                     selected = false
                    //                 )
                    //
                    //             }
                    //         }
                    //     ) {
                    //         app()
                    //     }
                    // }
                }
            }
        }
    }
}