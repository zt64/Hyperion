@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    jvmToolchain(11)
}

android {
    namespace = "com.hyperion"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
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

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.ktor)
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
    implementation(libs.kamel.image)
    debugImplementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.runtime.tracing)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.koin.compose)
    implementation(libs.ktor.okhttp)
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