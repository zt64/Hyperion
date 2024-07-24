package dev.zt64.hyperion.ui.screen

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.resources.MR
import dev.zt64.hyperion.ui.component.BackButton
import dev.zt64.hyperion.ui.component.ShareButton
import dev.zt64.hyperion.ui.model.ShortsScreenModel
import dev.zt64.hyperion.ui.tooling.HyperionPreview
import dev.zt64.innertube.domain.model.DomainVideoPartial

object ShortsScreen : Screen {
    @Composable
    override fun Content() {
        val model: ShortsScreenModel = koinScreenModel()

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackButton() },
                    title = { Text(stringResource(MR.strings.shorts)) },
                    actions = {
                        IconButton(onClick = model::showMenu) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(MR.strings.more)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            val shorts = model.shorts.collectAsLazyPagingItems()
            val pagerState = rememberPagerState { shorts.itemCount }

            VerticalPager(
                modifier = Modifier.padding(paddingValues),
                state = pagerState,
                key = { shorts[it]?.id ?: it }
            ) {
                val short = shorts[it] ?: return@VerticalPager

                Short(
                    short = short,
                    onClickLike = { /*TODO*/ },
                    onClickDislike = { /*TODO*/ },
                    onClickAuthor = { /*TODO*/ },
                    onClickComment = { /*TODO*/ },
                    onClickShare = { /*TODO*/ },
                    onClickSubscribe = { /*TODO*/ }
                )
            }
        }
    }

    @Composable
    fun Short(
        short: DomainVideoPartial,
        onClickLike: () -> Unit,
        onClickDislike: () -> Unit,
        onClickAuthor: () -> Unit,
        onClickComment: () -> Unit,
        onClickShare: () -> Unit,
        onClickSubscribe: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .combinedClickable(
                    onClick = {},
                    onDoubleClick = {}
                ).fillMaxSize()
        ) {
            // Player(player)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .widthIn(max = 300.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.clickable(onClick = onClickAuthor),
                            text = "Author name",
                            style = MaterialTheme.typography.titleMedium
                        )

                        TextButton(
                            enabled = false,
                            onClick = onClickSubscribe
                        ) {
                            Text(stringResource(MR.strings.subscribe))
                        }
                    }

                    Text(
                        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do " +
                            "eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onClickLike) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = stringResource(MR.strings.like)
                        )
                    }

                    Text("3.5K")

                    IconButton(onClick = onClickDislike) {
                        Icon(
                            imageVector = Icons.Default.ThumbDown,
                            contentDescription = stringResource(MR.strings.dislike)
                        )
                    }

                    Text("16")

                    IconButton(onClick = onClickComment) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = stringResource(MR.strings.comments)
                        )
                    }

                    ShareButton(short.shareUrl)
                }
            }
        }
    }
}

@Preview
@Composable
private fun ShortsScreenPreview() {
    HyperionPreview {
        ShortsScreen.Content()
    }
}