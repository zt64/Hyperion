package dev.zt64.hyperion.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.option.androidContext
import okio.Path.Companion.toOkioPath

@Composable
actual fun rememberImageLoader(): ImageLoader {
    val context = LocalContext.current

    return remember {
        ImageLoader {
            options {
                androidContext(context)
            }
            components {
                setupDefaultComponents()
            }
            interceptor {
                memoryCacheConfig {
                    // Set the max size to 25% of the app's available memory.
                    maxSizePercent(context, 0.25)
                }
                diskCacheConfig {
                    directory(context.cacheDir.toOkioPath().resolve("image_cache"))
                    maxSizeBytes(128L * 1024 * 1024) // 128MB
                }
            }
        }
    }
}