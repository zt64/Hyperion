package dev.zt64.hyperion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.component.BackButton

@Composable
fun ShortsScreen() {
    val pagerState = rememberPagerState { 10 }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton() },
                title = { Text(stringResource(MR.strings.shorts)) },
                actions = {
                    IconButton(
                        onClick = { /*TODO*/ }
                    ) {
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
        VerticalPager(
            modifier = Modifier.padding(paddingValues),
            state = pagerState
        ) { index ->
            Short(
                onClickLike = { /*TODO*/ },
                onClickDislike = { /*TODO*/ },
                onClickComment = { /*TODO*/ },
                onClickShare = { /*TODO*/ },
                onClickSubscribe = { /*TODO*/ }
            )
        }
    }
}

@Composable
fun Short(
    onClickLike: () -> Unit,
    onClickDislike: () -> Unit,
    onClickComment: () -> Unit,
    onClickShare: () -> Unit,
    onClickSubscribe: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
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
                        modifier = Modifier.clickable {

                        },
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
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
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
                        imageVector = Icons.AutoMirrored.Default.Comment,
                        contentDescription = stringResource(MR.strings.comments)
                    )
                }

                IconButton(onClick = onClickShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(MR.strings.share)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ShortsScreenPreview() {
    ShortsScreen()
}