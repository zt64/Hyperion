package dev.zt64.hyperion

import java.io.File

internal expect val SUPPORTS_DYNAMIC_COLOR: Boolean
internal expect val SUPPORTS_PIP: Boolean

internal expect class Platform() {
    fun getDownloadsDir(): File
}

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class CommonParcelize()

expect interface CommonParcelable