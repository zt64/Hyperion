package dev.zt64.hyperion.gradle

import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.exclude

fun ExternalModuleDependency.excludeMaterial2() {
    exclude(group = "org.jetbrains.compose.material", module = "material")
}