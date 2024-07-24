package dev.zt64.hyperion.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.DefaultModelEqualityDelegate
import coil3.compose.EqualityDelegate
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.valentinilk.shimmer.shimmer

@Composable
internal expect fun rememberImageLoader(): ImageLoader

@Composable
fun ShimmerImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    clipToBounds: Boolean = true,
    modelEqualityDelegate: EqualityDelegate = DefaultModelEqualityDelegate
) {
    val imageLoader = rememberImageLoader()

    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier,
        loading = {
            SubcomposeAsyncImageContent(
                modifier = Modifier.shimmer()
            )
        },
        error = {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error loading image",
                tint = MaterialTheme.colorScheme.error
            )
        },
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds,
        modelEqualityDelegate = modelEqualityDelegate
    )

    // CompositionLocalProvider(
    //     LocalImageLoader provides rememberImageLoader()
    // ) {
    //     val request = remember(url) { ImageRequest(url) }
    //
    //     val imageAction by rememberImageAction(request)
    //     val isLoading by remember { derivedStateOf { imageAction is ImageEvent } }
    //     val isError by remember { derivedStateOf { imageAction is ImageResult.OfError } }
    //     val painter = rememberImageActionPainter(
    //         action = imageAction,
    //         filterQuality = quality
    //     )
    //
    //     val backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
    //         elevation = LocalAbsoluteTonalElevation.current + 3.dp
    //     )
    //
    //     DisposableEffect(isError) {
    //         if (isError) {
    //             Napier.e(
    //                 tag = "Image",
    //                 message = "Failed to load image",
    //                 throwable = (imageAction as ImageResult.OfError).error
    //             )
    //         }
    //
    //         onDispose { }
    //     }
    //
    //     AsyncImage
    //
    //     Image(
    //         painter = painter,
    //         contentDescription = contentDescription,
    //         modifier =
    //             modifier
    //                 .then(if (isLoading) Modifier.shimmer() else Modifier)
    //                 .then(
    //                     if (isLoading || isError) {
    //                         Modifier.background(
    //                             backgroundColor
    //                         )
    //                     } else {
    //                         Modifier
    //                     }
    //                 ),
    //         alignment = alignment,
    //         contentScale = contentScale,
    //         alpha = alpha,
    //         colorFilter = colorFilter
    //     )
    // }
}