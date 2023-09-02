package dev.zt64.hyperion.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.*
import com.seiko.imageloader.model.ImageEvent
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.ImageResult
import dev.zt64.hyperion.ui.modifier.shimmer
import dev.zt64.hyperion.ui.theme.HyperionTheme
import dev.zt64.hyperion.ui.tooling.Preview
import io.github.aakira.napier.Napier

@Composable
fun ShimmerImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    quality: FilterQuality = FilterQuality.None,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    CompositionLocalProvider(
        LocalImageLoader provides rememberImageLoader()
    ) {
        val request = remember(url) {
            ImageRequest {
                data(url)
            }
        }

        val imageAction by rememberImageAction(request)
        val isLoading by remember { derivedStateOf { imageAction is ImageEvent } }
        val isError by remember { derivedStateOf { imageAction is ImageResult.Error } }
        val painter = rememberImageActionPainter(
            action = imageAction,
            filterQuality = quality,
        )

        val backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
            elevation = LocalAbsoluteTonalElevation.current + 3.dp
        )

        DisposableEffect(isError) {
            if (isError) {
                Napier.e(
                    tag = "Image",
                    message = "Failed to load image",
                    throwable = (imageAction as ImageResult.Error).error
                )
            }

            onDispose { }
        }

        Crossfade(
            targetState = painter,
            animationSpec = tween(300)
        ) {
            Image(
                painter = it,
                contentDescription = contentDescription,
                modifier = modifier.then(
                    if (isLoading) Modifier.shimmer() else Modifier
                ).then(
                    if (isLoading || isError) Modifier.background(backgroundColor) else Modifier
                ),
                alignment = alignment,
                contentScale = contentScale,
                alpha = alpha,
                colorFilter = colorFilter
            )
        }
    }
}

@Composable
expect fun rememberImageLoader(): ImageLoader

context(BoxScope)
@Composable
private fun Loading() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .shimmer(),
        color = MaterialTheme.colorScheme.secondaryContainer,
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