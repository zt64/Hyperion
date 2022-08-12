package com.hyperion.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@Composable
fun ChannelThumbnail(
    modifier: Modifier = Modifier,
    url: String,
) {
    SubcomposeAsyncImage(
        modifier = Modifier
            .clip(CircleShape)
            .then(modifier),
        model = url,
        loading = {
            val localElevation = LocalAbsoluteTonalElevation.current

            Box(
                modifier = modifier
                    .placeholder(
                        visible = true,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(localElevation + 2.dp),
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = MaterialTheme.colorScheme.surfaceColorAtElevation(localElevation + 3.dp)
                        )
                    )
                    .fillMaxSize(),
            )
        },
        success = {
            SubcomposeAsyncImageContent()
        },
        contentDescription = null
    )
}