package com.hyperion.ui.screen.root

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hyperion.R
import com.hyperion.ui.viewmodel.FeedViewModel

@Composable
fun FeedScreen(
    viewModel: FeedViewModel
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
                text = stringResource(R.string.feed_sign_in_title),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                modifier = Modifier.widthIn(max = 260.dp),
                text = stringResource(R.string.feed_sign_in_body),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Button(
                enabled = false,
                onClick = {}
            ) {
                Text(stringResource(R.string.sign_in))
            }
        }
    }

//    LazyColumn(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(10.dp)
//    ) {
//        stickyHeader {
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(2.dp)
//            ) {
//                LazyRow(
//                    contentPadding = PaddingValues(horizontal = 14.dp),
//                    horizontalArrangement = Arrangement.spacedBy(14.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    items(8) {
//                        Column(
//                            modifier = Modifier
//                                .clickable {
//
//                                }
//                                .padding(
//                                    horizontal = 4.dp,
//                                    vertical = 2.dp
//                                ),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            Box {
//                                ShimmerImage(
//                                    modifier = Modifier
//                                        .size(60.dp)
//                                        .clip(CircleShape),
//                                    model = "https://picsum.photos/200/300",
//                                    contentScale = ContentScale.Crop,
//                                    contentDescription = null
//                                )
//
//                                Box(
//                                    modifier = Modifier
//                                        .align(Alignment.BottomEnd)
//                                        .clip(CircleShape)
//                                        .background(MaterialTheme.colorScheme.primary)
//                                        .size(14.dp)
//                                )
//                            }
//
//                            Text(
//                                modifier = Modifier.width(64.dp),
//                                text = "Wing",
//                                style = MaterialTheme.typography.labelSmall,
//                                maxLines = 1,
//                                textAlign = TextAlign.Center,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                        }
//                    }
//
//                    item {
//                        OutlinedButton(onClick = { /*TODO*/ }) {
//                            Text("View all")
//                        }
//                    }
//                }
//
//                LazyRow(
//                    contentPadding = PaddingValues(horizontal = 14.dp),
//                    horizontalArrangement = Arrangement.spacedBy(10.dp)
//                ) {
//                    item {
//                        InputChip(
//                            selected = true,
//                            onClick = { /*TODO*/ },
//                            label = {
//                                Text("All videos")
//                            }
//                        )
//                    }
//
//                    item {
//                        InputChip(
//                            selected = false,
//                            onClick = { /*TODO*/ },
//                            label = {
//                                Text("Today")
//                            }
//                        )
//                    }
//
//                    item {
//                        InputChip(
//                            selected = false,
//                            onClick = { /*TODO*/ },
//                            label = {
//                                Text("Continue watching")
//                            }
//                        )
//                    }
//
//                    item {
//                        InputChip(
//                            selected = false,
//                            onClick = { /*TODO*/ },
//                            label = {
//                                Text("Unwatched")
//                            }
//                        )
//                    }
//
//                    item {
//                        InputChip(
//                            selected = false,
//                            onClick = { /*TODO*/ },
//                            label = {
//                                Text("Live")
//                            }
//                        )
//                    }
//
//                    item {
//                        InputChip(
//                            selected = false,
//                            onClick = { /*TODO*/ },
//                            label = {
//                                Text("Posts")
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
}