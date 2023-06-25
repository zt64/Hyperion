package com.hyperion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyperion.R

@Composable
fun ShortsScreen(
    onClickBack: () -> Unit
) {
    val pagerState = rememberPagerState { 10 }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = { Text(stringResource(R.string.shorts)) },
                actions = {
                    IconButton(
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.more)
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
            Short()
        }
    }
}

@Composable
fun Short() {
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
                        onClick = { /*TODO*/ }
                    ) {
                        Text(stringResource(R.string.subscribe))
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
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = stringResource(R.string.like)
                    )
                }

                Text("3.5K")

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = stringResource(R.string.dislike)
                    )
                }

                Text("16")

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = stringResource(R.string.comments)
                    )
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ShortsScreenPreview() {
    ShortsScreen {}
}