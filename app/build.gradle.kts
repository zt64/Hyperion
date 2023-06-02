@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
    id("kotlin-parcelize")
}

kotlin {
    jvmToolchain(11)
}

android {
    namespace = "com.hyperion"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "0.0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources {
            // Reflection symbol list (https://stackoverflow.com/a/41073782/13964629)
            excludes += "/**/*.kotlin_builtins"

            // okhttp3 is used by some lib (no cookies so publicsuffixes.gz can be dropped)
            excludes += "/okhttp3/**"
        }
    }

    androidComponents {
        onVariants(selector().withBuildType("release")) {
            it.packaging.resources.excludes.apply {
                // Debug metadata
                add("/**/*.version")
                add("/kotlin-tooling-metadata.json")
                // Kotlin debugging (https://github.com/Kotlin/kotlinx.coroutines/issues/2274)
                add("/DebugProbesKt.bin")
            }
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions.kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
}

dependencies {
    implementation(projects.innertube)
    implementation(projects.ktorBrotli)

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.media3)
    implementation(libs.bundles.accompanist)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.runtime)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.windowSizeClass)
    implementation(libs.compose.icons.extended)
    implementation(libs.compose.animation.graphics)
    implementation(libs.navigation)
    implementation(libs.coil.compose)
    debugImplementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.runtime.tracing)

    implementation(libs.ktor.core)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.android)
    implementation(libs.ktor.encoding)
    implementation(libs.ktor.contentnegotiation)
    implementation(libs.ktor.serialization.json)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.koin.compose)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-Xcontext-receivers"
        )
    }
}