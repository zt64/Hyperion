package dev.zt64.hyperion.ui.screen.base

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.icerock.moko.resources.compose.stringResource
import dev.zt64.hyperion.MR
import dev.zt64.hyperion.ui.LocalSnackbarHostState
import dev.zt64.hyperion.ui.component.ShimmerImage
import dev.zt64.hyperion.ui.component.player.WIDESCREEN_RATIO
import dev.zt64.hyperion.ui.model.LibraryScreenModel
import dev.zt64.hyperion.ui.screen.AddAccountScreen
import kotlinx.coroutines.launch

object LibraryTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(MR.strings.library)
            val icon = rememberVectorPainter(Icons.Default.VideoLibrary)

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
        val model: LibraryScreenModel = getScreenModel()
        val scope = rememberCoroutineScope()
        val snackbarHostState = LocalSnackbarHostState.currentOrThrow

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val navigator = LocalNavigator.currentOrThrow

                Icon(
                    modifier = Modifier.size(100.dp),
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null
                )

                Text(
                    text = stringResource(MR.strings.library_sign_in_title),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    modifier = Modifier.widthIn(max = 260.dp),
                    text = stringResource(MR.strings.library_sign_in_body),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("TODO")
                        }
                    }
                ) {
                    Text(stringResource(MR.strings.sign_in))
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
//                            Text(stringResource(MR.strings.history))
//                        },
//                        trailingContent = {
//                            OutlinedButton(onClick = { /*TODO*/ }) {
//                                Text(stringResource(MR.strings.view_all))
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
//                        Text(stringResource(MR.strings.playlists))
//                    },
//                    trailingContent = {
//                        OutlinedButton(onClick = { /*TODO*/ }) {
//                            Text(stringResource(MR.strings.recently_added))
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
}

