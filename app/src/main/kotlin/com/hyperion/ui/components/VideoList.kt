package com.hyperion.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hyperion.model.TrendingVideo
import com.hyperion.ui.screens.destinations.ChannelScreenDestination
import com.hyperion.ui.screens.destinations.PlayerScreenDestination
import com.hyperion.util.InvidiousApi
import com.ramcosta.composedestinations.navigation.navigateTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun VideoList(
    navController: NavController
) {
    val videos = remember { mutableStateListOf<TrendingVideo>() }

    if (videos.isEmpty()) {
        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) { videos.addAll(InvidiousApi.getTrending()) }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            content = { CircularProgressIndicator() }
        )
    } else {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(videos) { trendingVideo ->
                VideoCard(
                    video = trendingVideo,
                    onClick = { navController.navigateTo(PlayerScreenDestination(trendingVideo.videoId)) },
                    onChannelClick = { navController.navigateTo(ChannelScreenDestination(trendingVideo.authorUrl)) }
                )
            }
        }
    }
}