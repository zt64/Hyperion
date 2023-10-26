package dev.zt64.hyperion.ui.screen.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.component.ShimmerImage
import dev.zt64.hyperion.ui.model.FeedScreenModel
import dev.zt64.hyperion.ui.screen.AddAccountScreen
import dev.zt64.innertube.domain.model.DomainChannelPartial

object FeedTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(MR.strings.feed)
            val icon = rememberVectorPainter(Icons.Default.Subscriptions)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val model: FeedScreenModel = getScreenModel()
        val navController = LocalNavigator.currentOrThrow

        if (model.accountManager.loggedIn) {
            FeedScreenLoggedIn()
        } else {
            FeedScreenNotLoggedIn(
                onClickAddAccount = {
                    navController.push(AddAccountScreen)
                }
            )
        }
    }

    @Composable
    private fun FeedScreenNotLoggedIn(
        onClickAddAccount: () -> Unit
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    modifier = Modifier.size(100.dp),
                    imageVector = Icons.Default.Subscriptions,
                    contentDescription = null
                )

                Text(
                    text = stringResource(MR.strings.feed_sign_in_title),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    modifier = Modifier.widthIn(max = 260.dp),
                    text = stringResource(MR.strings.feed_sign_in_body),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Button(onClick = onClickAddAccount) {
                    Text(stringResource(MR.strings.sign_in))
                }
            }
        }
    }

    @Composable
    private fun FeedScreenLoggedIn() {
        val model: FeedScreenModel = getScreenModel()
        val items = model.items.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            stickyHeader {
                Column(
                    modifier = Modifier.fillParentMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ChannelsRow(
                        selectedChannel = null,
                        onClickChannel = {},
                        onClickViewAll = {}
                    )

                    FilterRow(
                        onClickFilter = {}
                    )
                }
            }

            items(
                count = items.itemCount,
                key = items.itemKey { it.hashCode() },
                contentType = items.itemContentType()
            ) { index ->
                val item = items[index] ?: return@items

            }
        }
    }

    @Composable
    private fun ChannelsRow(
        selectedChannel: DomainChannelPartial?,
        onClickChannel: () -> Unit,
        onClickViewAll: () -> Unit,
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(8) { index ->
                Column(
                    modifier = Modifier
                        .clickable(onClick = onClickChannel)
                        .padding(
                            horizontal = 4.dp,
                            vertical = 2.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier.toggleable(
                            value = true,
                            onValueChange = {}
                        )
                    ) {
                        ShimmerImage(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            url = "",
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .size(14.dp)
                        )
                    }

                    Text(
                        modifier = Modifier.width(64.dp),
                        text = "Channel",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            item {
                OutlinedButton(onClick = onClickViewAll) {
                    Text("View all")
                }
            }
        }
    }

    @Composable
    private fun FilterRow(onClickFilter: (String) -> Unit) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            fun chip(
                selected: Boolean,
                label: String
            ) {
                item {
                    FilterChip(
                        selected = selected,
                        onClick = { onClickFilter(label) },
                        label = { Text(label) }
                    )
                }
            }

            chip(
                selected = true,
                label = "All videos"
            )

            chip(
                selected = false,
                label = "Today"
            )

            chip(
                selected = false,
                label = "Continue watching"
            )

            chip(
                selected = false,
                label = "Unwatched"
            )

            chip(
                selected = false,
                label = "Live"
            )

            chip(
                selected = false,
                label = "Posts"
            )
        }
    }
}


