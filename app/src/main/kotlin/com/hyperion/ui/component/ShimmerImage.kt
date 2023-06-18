package com.hyperion.ui.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ShimmerImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    KamelImage(
        modifier = modifier,
        resource = asyncPainterResource(url),
        contentDescription = contentDescription,
        contentScale = contentScale,
        onLoading = {
            val localElevation = LocalAbsoluteTonalElevation.current

            Box(
                modifier = Modifier
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(localElevation + 1.dp),
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                localElevation + 4.dp
                            )
                        )
                    )
                    .fillMaxSize()
            )
        },
        onFailure = {
            Icon(
                modifier = Modifier.size(64.dp),
                imageVector = Icons.Default.BrokenImage,
                contentDescription = null,
            )
        },
        animationSpec = tween()
    )
}