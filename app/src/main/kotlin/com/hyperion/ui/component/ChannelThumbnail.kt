package com.hyperion.ui.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage

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
        model = url,
        contentDescription = null
    )
}