package dev.zt64.hyperion.gradle

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.Locale

fun KotlinMultiplatformExtension.apple(
    skipCheck: Boolean = false,
    configure: KotlinNativeTarget.() -> Unit = {}
) {
    if (!skipCheck) {
        val isMacOs = System
            .getProperty("os.name")
            .lowercase(Locale.getDefault())
            .contains("mac")

        if (!isMacOs) return
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64()
    ).forEach(configure)
}