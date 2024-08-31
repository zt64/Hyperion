package dev.zt64.hyperion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import dev.zt64.hyperion.ui.component.ShimmerImage
import dev.zt64.hyperion.ui.theme.HyperionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Hyperion()
        }
    }
}

@Composable
fun Hyperion(modifier: Modifier = Modifier) {
    HyperionTheme {
        Surface(
            modifier = modifier.fillMaxSize()
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
                        fun Item(label: String, icon: ImageVector, selected: Boolean = false) {
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
                                            onClick = { selected = !selected }
                                        ),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null
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
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    items(5) { content ->
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(10) { cardItem ->
                                StandardCardContainer(
                                    modifier = Modifier.width(200.dp),
                                    imageCard = {
                                        Card(
                                            onClick = {}
                                        ) {
                                            ShimmerImage(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(16f / 9f),
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