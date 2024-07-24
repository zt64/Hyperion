package dev.zt64.hyperion

import java.io.File

internal actual const val SUPPORTS_DYNAMIC_COLOR: Boolean = false
internal actual const val SUPPORTS_GESTURES: Boolean = false
internal actual const val SUPPORTS_PIP: Boolean = true

internal actual object Platform {
    actual fun getDownloadsDir(): File = File(System.getProperty("user.home"), "Downloads")
}