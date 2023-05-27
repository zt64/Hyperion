package com.hyperion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hyperion.R

@Composable
fun NotificationsScreen(
    onClickBack: () -> Unit
) {
    val notificationsSheetState = remember { SheetState() }

    NotificationSheet(notificationsSheetState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notifications)) },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        NotificationsScreenEmpty(
            modifier = Modifier.padding(paddingValues)
        )

//        LazyColumn(
//            modifier = Modifier.padding(paddingValues)
//        ) {
//            items(10) { index ->
//                val notification = remember {
//                    index.toString()
//                }
//
//                Notification(
//                    notification = notification,
//                    onClick = { },
//                    onLongClick = { notificationsSheetState.expand(notification) }
//                )
//            }
//        }
    }
}

@Composable
private fun NotificationsScreenEmpty(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = null
        )

        Text(
            text = "Your notifications live here",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            modifier = Modifier.widthIn(max = 260.dp),
            text = "Subscribe to your favorite channels to get notified about their latest videos.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Notification(
    notification: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ),
        headlineContent = {
            BadgedBox(
                badge = {
                    Badge(
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            ) {
                Text("Notification $notification")
            }
        },
        supportingContent = {
            Text("7 days ago")
        },
        leadingContent = {

        },
        trailingContent = {

        }
    )
}

@Composable
private fun NotificationSheet(sheetState: SheetState) {
    if (!sheetState.expanded) return

    ModalBottomSheet(onDismissRequest = sheetState::collapse) {
        ListItem(
            modifier = Modifier.clickable { },
            leadingContent = {
                 Icon(
                    imageVector = Icons.Default.RemoveCircle,
                    contentDescription = null
                )
            },
            headlineContent = { Text(stringResource(R.string.hide_this_notification)) }
        )

        ListItem(
            modifier = Modifier.clickable { },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.NotificationsOff,
                    contentDescription = null
                )
            },
            headlineContent = { Text("Turn off all from ${sheetState.notification}") }
        )

        ListItem(
            modifier = Modifier.clickable { },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.NotificationsOff,
                    contentDescription = null
                )
            },
            headlineContent = { Text("Turn off all recommendation notifications") }
        )
    }
}

@Stable
private class SheetState {
    var expanded by mutableStateOf(false)
        private set
    var notification by mutableStateOf("")
        private set

    fun expand(notification: String) {
        expanded = true
        this.notification = notification
    }

    fun collapse() {
        expanded = false
    }
}