package dev.zt64.hyperion.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader

@Composable
internal actual fun rememberImageLoader(): ImageLoader {
    val context = LocalContext.current

    return remember {
        ImageLoader(context)
        // CommonImageLoader {
        //     options {
        //         androidContext(context)
        //     }
        //
        //     interceptor {
        //         memoryCacheConfig {
        //             // Set the max size to 25% of the app's available memory.
        //             maxSizePercent(context, 0.25)
        //         }
        //         diskCacheConfig {
        //             directory(context.cacheDir.toOkioPath().resolve("image_cache"))
        //         }
        //     }
        // }
    }
}