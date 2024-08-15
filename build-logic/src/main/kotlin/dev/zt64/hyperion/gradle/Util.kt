package dev.zt64.hyperion.gradle

import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.exclude

/**
 * Some libraries unnecessarily depend on material 2 for no reason... so we need to exclude it
 */
fun ExternalModuleDependency.excludeMaterial2() {
    exclude(group = "org.jetbrains.compose.material", module = "material")
}