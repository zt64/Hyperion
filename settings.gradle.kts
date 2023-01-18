enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

rootProject.name = "Hyperion"
include(":app")
include(":innertube")
include(":ktor-brotli")