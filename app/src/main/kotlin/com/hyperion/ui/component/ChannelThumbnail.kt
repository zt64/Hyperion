package com.hyperion.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.hyperion.util.shimmer

@Composable
fun ChannelThumbnail(
    modifier: Modifier = Modifier,
    url: String
) {
    SubcomposeAsyncImage(
        modifier = Modifier
            .clip(CircleShape)
            .then(modifier),
        model = url,
        loading = {
            Box(
                modifier = modifier
                    .shimmer()
                    .fillMaxSize(),
            )
        },
        success = {
            SubcomposeAsyncImageContent()
        },
        contentDescription = null
    )
}