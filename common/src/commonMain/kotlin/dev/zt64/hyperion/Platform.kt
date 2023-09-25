package dev.zt64.hyperion

import java.io.File

internal expect val SUPPORTS_DYNAMIC_COLOR: Boolean
internal expect val SUPPORTS_PIP: Boolean

internal expect object Platform {
    fun getDownloadsDir(): File
}

// Somehow fixes a bug
expect interface CommonParcelable