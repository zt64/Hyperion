package com.hyperion.ui.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ChannelThumbnail(
    modifier: Modifier = Modifier,
    url: String,
) {
    // TODO: Add tinted placeholder icon
    AsyncImage(
        modifier = Modifier
            .clip(CircleShape)
            .then(modifier),
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = null
    )
}