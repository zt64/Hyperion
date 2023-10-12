package dev.zt64.hyperion.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.option.androidContext
import okio.Path.Companion.toOkioPath

@Composable
internal actual fun rememberImageLoader(): ImageLoader {
    val context = LocalContext.current

    return remember {
        dev.zt64.hyperion.ui.component.ImageLoader {
            options {
                androidContext(context)
            }

            interceptor {
                memoryCacheConfig {
                    // Set the max size to 25% of the app's available memory.
                    maxSizePercent(context, 0.25)
                }
                diskCacheConfig {
                    directory(context.cacheDir.toOkioPath().resolve("image_cache"))
                }
            }
        }
    }
}