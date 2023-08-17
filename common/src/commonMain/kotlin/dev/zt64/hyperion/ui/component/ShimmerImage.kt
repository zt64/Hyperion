package dev.zt64.hyperion.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.*
import com.seiko.imageloader.model.ImageEvent
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.ImageResult
import dev.zt64.hyperion.ui.modifier.shimmer
import dev.zt64.hyperion.ui.theme.HyperionTheme
import dev.zt64.hyperion.ui.tooling.Preview

@Composable
fun ShimmerImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    CompositionLocalProvider(
        LocalImageLoader provides rememberImageLoader()
    ) {
        val request = remember(url) {
            ImageRequest {
                data(url)
            }
        }

        val action by rememberImageAction(request)
        val painter = rememberImageActionPainter(action)

        Box(
            contentAlignment = Alignment.Center
        ) {
            when (val current = action) {
                is ImageEvent.Progress -> {
                    CircularProgressIndicator()
                }

                is ImageResult.Error -> {
                    Text(current.error.message ?: "Error")
                }

                else -> Unit
            }

            Image(
                modifier = modifier,
                painter = painter,
                contentDescription = contentDescription,
                contentScale = contentScale
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