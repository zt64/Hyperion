package dev.zt64.hyperion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.material3.*
import dev.zt64.hyperion.ui.component.ShimmerImage

@OptIn(ExperimentalTvMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurface
                    ) {
                        val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)

                        NavigationDrawer(
                            drawerState = drawerState,
                            drawerContent = {
                                Column(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Min)
                                        .fillMaxHeight()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {


                                    @Composable
                                    fun Item(
                                        label: String,
                                        icon: ImageVector,
                                        selected: Boolean = false,
                                    ) {
                                        var selected by remember { mutableStateOf(selected) }
                                        val elevation by animateDpAsState(
                                            targetValue = if (selected) 8.dp else 0.dp,
                                            label = ""
                                        )

                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            tonalElevation = elevation,
                                            shape = CircleShape
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(12.dp)
                                                    .selectable(
                                                        selected = selected,
                                                        onClick = { selected = !selected },
                                                    ),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                )

                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                        }

                                    }

                                    Item(
                                        label = "Search",
                                        icon = Icons.Default.Search
                                    )

                                    Item(
                                        label = "Home",
                                        icon = Icons.Default.Home,
                                        selected = true
                                    )

                                    Item(
                                        label = "Subscriptions",
                                        icon = Icons.Default.Subscriptions
                                    )

                                    Item(
                                        label = "Library",
                                        icon = Icons.Default.VideoLibrary
                                    )

                                    Spacer(Modifier.weight(1f, true))

                                    Item(
                                        label = "Settings",
                                        icon = Icons.Default.Settings
                                    )
                                }
                            }
                        ) {
                            TvLazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(40.dp)
                            ) {
                                items(5) { content ->
                                    TvLazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(10) { cardItem ->
                                            StandardCardLayout(
                                                modifier = Modifier.width(200.dp),
                                                imageCard = {
                                                    CardLayoutDefaults.ImageCard(
                                                        onClick = {},
                                                        interactionSource = remember { MutableInteractionSource() },
                                                    ) {
                                                        ShimmerImage(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .aspectRatio(16f/9f),
                                                            url = "https://loremflickr.com/640/360",
                                                            contentDescription = null
                                                        )
                                                    }
                                                },
                                                title = {
                                                    Text(
                                                        text = "Cat",
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                },
                                                description = {
                                                    Text(
                                                        text = "20M views | 1 year ago",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                },
                                                subtitle = {
                                                    Text(
                                                        text = "Rushiimachine",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}