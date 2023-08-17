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
import androidx.compose.ui.zIndex
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.ui.navigation.AppDestination
import dev.zt64.hyperion.ui.navigation.BaseDestination
import dev.zt64.hyperion.ui.navigation.Destination
import dev.zt64.hyperion.ui.theme.HyperionTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
suspend fun main() {
    // println(MPVLib)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Hyperion"
        ) {
            HyperionTheme(true) {
                Surface {
                    val scope = rememberCoroutineScope()
                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    var selectedDestination by remember {
                        mutableStateOf<Destination>(BaseDestination.HOME)
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        gesturesEnabled = false,
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier.padding(16.dp),
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 4.dp
                                    ).zIndex(99f),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {

                                            },
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
                                                text = "Guh",
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
                                            icon = {
                                                Icon(
                                                    imageVector = destination.icon,
                                                    contentDescription = null
                                                )
                                            },
                                            label = { Text(stringResource(destination.label)) },
                                            onClick = {
                                                selectedDestination = destination

                                                scope.launch {
                                                    drawerState.close()
                                                }
                                            },
                                            selected = selectedDestination == destination
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    NavigationDrawerItem(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = null
                                            )
                                        },
                                        label = { Text(stringResource(MR.strings.settings)) },
                                        onClick = {
                                            scope.launch {
                                                drawerState.close()
                                            }
                                        },
                                        selected = selectedDestination == AppDestination.Settings
                                    )

                                }
                            }
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    navigationIcon = {
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    drawerState.open()
                                                }
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

@Composable
fun Player(
    modifier: Modifier = Modifier
) {

}

// 00000000000b29f0 T mpv_abort_async_command
// 00000000000b4360 T mpv_client_api_version
// 00000000000aa780 T mpv_client_id
// 00000000000aa770 T mpv_client_name
// 00000000000b13a0 T mpv_command
// 00000000000b2990 T mpv_command_async
// 00000000000b2860 T mpv_command_node
// 00000000000b29c0 T mpv_command_node_async
// 00000000000b28d0 T mpv_command_ret
// 00000000000b2940 T mpv_command_string
// 00000000000b1aa0 T mpv_create
// 00000000000b27d0 T mpv_create_client
// 00000000000b2810 T mpv_create_weak_client
// 00000000000b2b20 T mpv_del_property
// 00000000000b1a60 T mpv_destroy
// 00000000000b4370 T mpv_error_string
// 00000000000b43b0 T mpv_event_name
// 00000000000b44e0 T mpv_event_to_node
// 00000000000b4920 T mpv_free
// 00000000000b0ea0 T mpv_free_node_contents
// 00000000000b2d20 T mpv_get_property
// 00000000000b2ec0 T mpv_get_property_async
// 00000000000b2e70 T mpv_get_property_osd_string
// 00000000000b2e20 T mpv_get_property_string
// 00000000000b4930 T mpv_get_time_us
// 00000000000b42b0 T mpv_get_wakeup_pipe
// 00000000000b3d90 T mpv_hook_add
// 00000000000b4da0 T mpv_hook_continue
// 00000000000aef30 T mpv_initialize
// 00000000000b3f30 T mpv_load_config_file
// 00000000000b2fc0 T mpv_observe_property
// 00000000001179f0 T mpv_render_context_create
// 00000000001152a0 T mpv_render_context_free
// 0000000000113320 T mpv_render_context_get_info
// 0000000000117130 T mpv_render_context_render
// 0000000000113250 T mpv_render_context_report_swap
// 0000000000113300 T mpv_render_context_set_parameter
// 0000000000113200 T mpv_render_context_set_update_callback
// 00000000001132b0 T mpv_render_context_update
// 00000000000b43e0 T mpv_request_event
// 00000000000b3fa0 T mpv_request_log_messages
// 00000000000b1270 T mpv_set_option
// 00000000000b1370 T mpv_set_option_string
// 00000000000b2a10 T mpv_set_property
// 00000000000b2bb0 T mpv_set_property_async
// 00000000000b2b80 T mpv_set_property_string
// 00000000000ae950 T mpv_set_wakeup_callback
// 00000000000b49c0 T mpv_stream_cb_add_ro
// 00000000000b1a80 T mpv_terminate_destroy
// 00000000000b3310 T mpv_unobserve_property
// 00000000000ae9b0 T mpv_wait_async_requests
// 00000000000b0960 T mpv_wait_event
// 00000000000b0e60 T mpv_wakeup
object MPVLib {
    init {
        System.loadLibrary("mpv")
        mpv_initialize()
    }

    private external fun mpv_initialize(): Long
}
