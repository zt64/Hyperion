package dev.zt64.hyperion.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import kotlin.io.path.Path

@Composable
internal actual fun rememberImageLoader(): ImageLoader {
    val context = LocalPlatformContext.current
    return remember {
        ImageLoader
            .Builder(context)
            .build()
    }
}

private fun getCacheDir() = Path(System.getProperty("user.home")).resolve(".cache/hyperion")