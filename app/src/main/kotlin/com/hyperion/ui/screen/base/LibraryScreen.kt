package com.hyperion.ui.screen.base

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyperion.R
import com.hyperion.ui.component.ShimmerImage
import com.hyperion.ui.component.player.WIDESCREEN_RATIO
import com.hyperion.ui.viewmodel.LibraryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryScreen() {
    val viewModel: LibraryViewModel = koinViewModel()

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
                imageVector = Icons.Default.VideoLibrary,
                contentDescription = null
            )

            Text(
                text = stringResource(R.string.library_sign_in_title),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                modifier = Modifier.widthIn(max = 260.dp),
                text = stringResource(R.string.library_sign_in_body),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Button(
                enabled = false,
                onClick = { }
            ) {
                Text(stringResource(R.string.sign_in))
            }
        }
    }

//    Surface(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        LazyColumn {
//            item {
//                Column {
//                    ListItem(
//                        headlineContent = {
//                            Text(stringResource(R.string.history))
//                        },
//                        trailingContent = {
//                            OutlinedButton(onClick = { /*TODO*/ }) {
//                                Text(stringResource(R.string.view_all))
//                            }
//                        }
//                    )
//
//                    LazyRow(
//                        contentPadding = PaddingValues(
//                            start = 14.dp,
//                            end = 14.dp
//                        ),
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        // TODO
//                    }
//                }
//            }
//
//            item {
//                ListItem(
//                    headlineContent = {
//                        Text(stringResource(R.string.playlists))
//                    },
//                    trailingContent = {
//                        OutlinedButton(onClick = { /*TODO*/ }) {
//                            Text(stringResource(R.string.recently_added))
//                        }
//                    }
//                )
//            }
//        }
//    }
}

@Composable
private fun RecentCard(
    title: String,
    subtitle: String,
    duration: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .size(
                width = 144.dp,
                height = 148.dp
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Column {
            Box {
                ShimmerImage(
                    modifier = Modifier
                        .background(Color.Black)
                        .aspectRatio(WIDESCREEN_RATIO),
                    url = "https://cdn.discordapp.com/avatars/556614860931072012/f50b1c6daf6f00827f827113e9920677.webp?size=480",
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
                        text = duration,
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