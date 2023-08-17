@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Hyperion"
include(":innertube")
include(":ktor-brotli")

include(
    ":common",
    ":desktop"
)

include(
    ":android",
    ":android:tv",
    ":android:mobile",
    ":android:wear",
)