package dev.zt64.hyperion.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.AdaptiveTopBar
import dev.zt64.hyperion.ui.component.ShimmerImage
import dev.zt64.hyperion.ui.model.NotificationsScreenModel
import dev.zt64.innertube.domain.model.Notification

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

object NotificationsScreen : Screen {
    @Composable
    override fun Content() {
        val model: NotificationsScreenModel = koinScreenModel()
        val notificationsSheetState = remember { SheetState() }

        // NotificationSheet(notificationsSheetState)

        Scaffold(
            topBar = {
                AdaptiveTopBar(
                    title = { Text(stringResource(MR.strings.notifications)) }
                )
            }
        ) { paddingValues ->
            val notifications = model.notifications.collectAsLazyPagingItems()

            if (notifications.itemCount == 0) {
                NotificationsScreenEmpty(
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                val state = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    state = state
                ) {
                    items(
                        count = notifications.itemCount,
                        key = notifications.itemKey { it.id },
                        contentType = notifications.itemContentType()
                    ) { index ->
                        val notification = notifications[index] ?: return@items
                        val dismissState = rememberSwipeToDismissBoxState()

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.Settled -> {
                                            MaterialTheme.colorScheme.surface
                                        }
                                        SwipeToDismissBoxValue.StartToEnd -> Color.Red
                                        SwipeToDismissBoxValue.EndToStart -> Color.Red
                                    }
                                )
                                Box(Modifier.fillMaxSize().background(color))
                            }
                        ) {
                            Notification(
                                notification = notification,
                                onClick = { },
                                onLongClick = { notificationsSheetState.expand(notification) }
                            )
                        }
                    }
                }
            }
        }
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

// @Composable
// private fun NotificationSheet(sheetState: androidx.compose.material3.SheetState) {
//     if (!sheetState.expanded) return
//
//     ModalBottomSheet(onDismissRequest = sheetState::collapse) {
//         ListItem(
//             modifier = Modifier.clickable { },
//             leadingContent = {
//                 Icon(
//                     imageVector = Icons.Default.RemoveCircle,
//                     contentDescription = stringResource(MR.strings.remove)
//                 )
//             },
//             headlineContent = { Text(stringResource(MR.strings.hide_this_notification)) }
//         )
//
//         ListItem(
//             modifier = Modifier.clickable { },
//             leadingContent = {
//                 Icon(
//                     imageVector = Icons.Default.NotificationsOff,
//                     contentDescription = null
//                 )
//             },
//             headlineContent = { Text("Turn off all from ${sheetState.notification!!.content}") }
//         )
//
//         ListItem(
//             modifier = Modifier.clickable { },
//             leadingContent = {
//                 Icon(
//                     imageVector = Icons.Default.NotificationsOff,
//                     contentDescription = null
//                 )
//             },
//             headlineContent = { Text("Turn off all recommendation notifications") }
//         )
//     }
// }