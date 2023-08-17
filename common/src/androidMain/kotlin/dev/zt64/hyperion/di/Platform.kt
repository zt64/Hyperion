package dev.zt64.hyperion.di

import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import java.io.File

internal actual class Platform actual constructor() {
    actual fun getDownloadsDir(): File {
        return getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
    }
}