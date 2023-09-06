package dev.zt64.hyperion

import android.os.Build
import android.os.Environment
import android.os.Parcelable
import androidx.annotation.ChecksSdkIntAtLeast
import java.io.File

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
actual val SUPPORTS_DYNAMIC_COLOR: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
actual val SUPPORTS_PIP: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

internal actual class Platform {
    actual fun getDownloadsDir(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }
}

actual typealias CommonParcelable = Parcelable