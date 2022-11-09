package com.hyperion.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.hyperion.util.shimmer

@Composable
fun ShimmerImage(
    modifier: Modifier = Modifier,
    model: Any?,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String?
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        model = model,
        contentScale = contentScale,
        contentDescription = contentDescription,
        loading = {
            Box(
                modifier = Modifier
                    .shimmer()
                    .fillMaxSize()
            )
        },
        success = {
            SubcomposeAsyncImageContent()
        }
    )
}