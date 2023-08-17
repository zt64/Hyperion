package dev.zt64.hyperion.di

import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs
import java.io.File

internal actual class Platform actual constructor() {
    actual fun getDownloadsDir(): File {
        when (hostOs) {
            OS.Linux -> TODO()
            OS.Windows -> TODO()
            OS.MacOS -> TODO()
            else -> error("Unsupported OS: $hostOs")
        }
    }
}