package com.hyperion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hyperion.R

@Composable
fun LibraryScreen() {
    LazyColumn {
        item {
            Column {
                ListItem(
                    headlineText = {
                        Text(stringResource(R.string.history))
                    },
                    trailingContent = {
                        OutlinedButton(onClick = { /*TODO*/ }) {
                            Text(stringResource(R.string.view_all))
                        }
                    }
                )

                LazyRow(
                    contentPadding = PaddingValues(
                        start = 14.dp,
                        end = 14.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // TODO
                }
            }
        }

        item {
            ListItem(
                headlineText = {
                    Text(stringResource(R.string.playlists))
                },
                trailingContent = {
                    OutlinedButton(onClick = { /*TODO*/ }) {
                        Text(stringResource(R.string.recently_added))
                    }
                }
            )
        }
    }
}

@Composable
private fun RecentCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .size(
                width = 144.dp,
                height = 160.dp
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Column {
            Box {
                AsyncImage(
                    modifier = Modifier
                        .background(Color.Black)
                        .aspectRatio(16f / 9f),
                    model = "https://cdn.discordapp.com/avatars/556614860931072012/f50b1c6daf6f00827f827113e9920677.webp?size=480",
                    contentScale = ContentScale.Fit,
                    contentDescription = null
                )

                Surface(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.BottomEnd),
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
                ) {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = "5:47",
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Column(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 4.dp
                )
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp
                    )
                )

                Text(
                    text = subtitle,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}