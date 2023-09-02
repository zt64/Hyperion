package dev.zt64.hyperion.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import okio.Path.Companion.toOkioPath
import java.io.File

@Composable
actual fun rememberImageLoader(): ImageLoader = remember {
    ImageLoader {
        components {
            setupDefaultComponents()
        }
        interceptor {
            memoryCacheConfig {
                maxSizeBytes(32 * 1024 * 1024) // 32MB
            }
            diskCacheConfig {
                directory(getCacheDir().toOkioPath().resolve("image_cache"))
                maxSizeBytes(128L * 1024 * 1024) // 128MB
            }
        }
    } }

private fun getCacheDir() = File(System.getProperty("user.home"), ".cache/hyperion")