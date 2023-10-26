package dev.zt64.hyperion.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.*
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.model.ImageEvent
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.ImageResult
import com.valentinilk.shimmer.shimmer
import io.github.aakira.napier.Napier

@Composable
internal expect fun rememberImageLoader(): ImageLoader

fun ImageLoader(block: ImageLoaderConfigBuilder.() -> Unit): ImageLoader {
    return com.seiko.imageloader.ImageLoader {
        components {
            setupDefaultComponents()
        }

        interceptor {
            diskCacheConfig {
                maxSizeBytes(128L * 1024 * 1024) // 128MB
            }
        }

        block()
    }
}

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
            ImageRequest(url)
        }

        val imageAction by rememberImageAction(request)
        val isLoading by remember { derivedStateOf { imageAction is ImageEvent } }
        val isError by remember { derivedStateOf { imageAction is ImageResult.Error } }
        val painter = rememberImageActionPainter(
            action = imageAction,
            filterQuality = quality
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

        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier
                .then(if (isLoading) Modifier.shimmer() else Modifier)
                .then(if (isLoading || isError) Modifier.background(backgroundColor) else Modifier),
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter
        )
    }
}