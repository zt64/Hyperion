package dev.zt64.hyperion.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.seiko.imageloader.ImageLoader
import okio.Path.Companion.toOkioPath
import kotlin.io.path.Path

@Composable
actual fun rememberImageLoader(): ImageLoader = remember {
    ImageLoader {
        configureCommon()

        interceptor {
            memoryCacheConfig {
                maxSizeBytes(32 * 1024 * 1024) // 32MB
            }
            diskCacheConfig {
                directory(getCacheDir().toOkioPath().resolve("image_cache"))
            }
        }
    }
}

private fun getCacheDir() = Path(System.getProperty("user.home")).resolve(".cache/hyperion")