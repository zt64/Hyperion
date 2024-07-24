package dev.zt64.hyperion

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import java.io.File

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
internal actual val SUPPORTS_DYNAMIC_COLOR: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
internal actual val SUPPORTS_PIP: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

internal actual val SUPPORTS_GESTURES: Boolean = true

internal actual object Platform {
    actual fun getDownloadsDir(): File = File("/storage/emulated/0/Download")
}