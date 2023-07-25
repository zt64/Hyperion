package com.hyperion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.hyperion.R
import com.hyperion.ui.component.BackButton
import com.hyperion.ui.component.ShimmerImage
import com.hyperion.ui.viewmodel.NotificationsViewModel
import com.zt.innertube.domain.model.Notification
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotificationsScreen() {
    val viewModel: NotificationsViewModel = koinViewModel()
    val notificationsSheetState = remember { SheetState() }

    NotificationSheet(notificationsSheetState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notifications)) },
                navigationIcon = { BackButton() }
            )
        }
    ) { paddingValues ->
        val notifications = viewModel.notifications.collectAsLazyPagingItems()

        // NotificationsScreenEmpty(
        //     modifier = Modifier.padding(paddingValues)
        // )

        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(
                count = notifications.itemCount,
                key = notifications.itemKey { it.id },
                contentType = notifications.itemContentType()
            ) { index ->
                val notification = notifications[index] ?: return@items

                Notification(
                    notification = notification,
                    onClick = { },
                    onLongClick = { notificationsSheetState.expand(notification) }
                )
            }
        }
    }
}

@Composable
private fun NotificationsScreenEmpty(modifier: Modifier = Modifier) {
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
    notification: Notification,
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
                Text(notification.header)
            }
        },
        supportingContent = notification.content?.let {
            {
                Text(it)
            }
        },
        leadingContent = {
            ShimmerImage(
                url = notification.leadingImage,
                contentDescription = null
            )
        },
        trailingContent = {
            ShimmerImage(
                url = notification.trailingImage,
                contentDescription = null
            )
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
                    contentDescription = stringResource(R.string.remove)
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
            headlineContent = { Text("Turn off all from ${sheetState.notification!!.content}") }
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
    var notification by mutableStateOf<Notification?>(null)
        private set

    fun expand(notification: Notification) {
        expanded = true
        this.notification = notification
    }

    fun collapse() {
        expanded = false
    }
}