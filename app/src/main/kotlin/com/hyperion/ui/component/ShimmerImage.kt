package com.hyperion.ui.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyperion.ui.theme.HyperionTheme
import com.valentinilk.shimmer.shimmer
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.stringMapper
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.imageBitmapDecoder

private val kamelConfig = KamelConfig {
    imageBitmapDecoder()
    stringMapper()
    httpFetcher()
}

@Composable
fun ShimmerImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    CompositionLocalProvider(
        LocalKamelConfig provides kamelConfig
    ) {
        KamelImage(
            modifier = modifier,
            resource = asyncPainterResource(url),
            contentDescription = contentDescription,
            contentScale = contentScale,
            onLoading = { Loading() },
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
}

context(BoxScope)
@Composable
private fun Loading() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .shimmer(),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        content = {},
    )
}

@Preview
@Composable
private fun LoadingPreview() {
    HyperionTheme {
        Surface(
            modifier = Modifier.size(100.dp, 100.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(0.5f)
            ) {
                Loading()
            }
        }
    }
}