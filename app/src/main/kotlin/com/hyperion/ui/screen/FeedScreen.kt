package com.hyperion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hyperion.ui.viewmodel.FeedViewModel

@Composable
fun FeedScreen(
    viewModel: FeedViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stickyHeader {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(8) {
                        Column(
                            modifier = Modifier
                                .clickable {

                                }
                                .padding(
                                    horizontal = 4.dp,
                                    vertical = 2.dp
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box {
                                AsyncImage(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape),
                                    model = "https://picsum.photos/200/300",
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
                                text = "Wing",
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    item {
                        OutlinedButton(onClick = { /*TODO*/ }) {
                            Text("View all")
                        }
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        InputChip(
                            selected = true,
                            onClick = { /*TODO*/ },
                            label = {
                                Text("All videos")
                            }
                        )
                    }

                    item {
                        InputChip(
                            selected = false,
                            onClick = { /*TODO*/ },
                            label = {
                                Text("Today")
                            }
                        )
                    }

                    item {
                        InputChip(
                            selected = false,
                            onClick = { /*TODO*/ },
                            label = {
                                Text("Continue watching")
                            }
                        )
                    }

                    item {
                        InputChip(
                            selected = false,
                            onClick = { /*TODO*/ },
                            label = {
                                Text("Unwatched")
                            }
                        )
                    }

                    item {
                        InputChip(
                            selected = false,
                            onClick = { /*TODO*/ },
                            label = {
                                Text("Live")
                            }
                        )
                    }

                    item {
                        InputChip(
                            selected = false,
                            onClick = { /*TODO*/ },
                            label = {
                                Text("Posts")
                            }
                        )
                    }
                }
            }
        }
    }
}