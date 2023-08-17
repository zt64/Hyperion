package dev.zt64.hyperion.ui.component

import androidx.compose.runtime.Composable
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import okio.Path.Companion.toOkioPath
import java.io.File

@Composable
actual fun rememberImageLoader(): ImageLoader = ImageLoader {
    components {
        setupDefaultComponents()
    }
    interceptor {
        memoryCacheConfig {
            maxSizeBytes(32 * 1024 * 1024) // 32MB
        }
        diskCacheConfig {
            directory(getCacheDir().toOkioPath().resolve("image_cache"))
            maxSizeBytes(512L * 1024 * 1024) // 512MB
        }
    }
}

// about currentOperatingSystem, see app
private fun getCacheDir() = File(System.getProperty("user.home"), ".cache/hyperion")