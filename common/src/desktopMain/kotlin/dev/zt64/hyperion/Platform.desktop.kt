package dev.zt64.hyperion

import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs
import java.io.File

internal actual const val SUPPORTS_DYNAMIC_COLOR: Boolean = false
internal actual const val SUPPORTS_PIP: Boolean = true

internal actual class Platform {
    actual fun getDownloadsDir(): File {
        when (hostOs) {
            OS.Linux -> TODO()
            OS.Windows -> TODO()
            OS.MacOS -> TODO()
            else -> error("Unsupported OS: $hostOs")
        }
    }
}

actual interface CommonParcelable