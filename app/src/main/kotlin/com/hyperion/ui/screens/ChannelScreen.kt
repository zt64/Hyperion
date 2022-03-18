package com.hyperion.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.ramcosta.composedestinations.annotation.Destination

private enum class ChannelTabs {
    HOME,
}

@Destination
@Composable
fun ChannelScreen(
    channelUrl: String
) {
    Column {


        Row {
            // AsyncImage
            Column {

            }
        }

        var selectedTabIndex by remember { mutableStateOf(0) }
        val titles = listOf("TAB 1", "TAB 2", "TAB 3 WITH LOTS OF TEXT")

        TabRow(
            selectedTabIndex = selectedTabIndex,
            tabs = {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        text = { Text(title)},
                        icon = { Icon(Icons.Default.Home, "Home") },
                        onClick = {

                        }
                    )
                }
            }
        )

        LazyColumn {

        }
    }
}