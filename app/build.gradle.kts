val pagingVersion = "3.1.0"
val composeVersion = "1.2.0-rc01"
val ktorVersion = "2.0.2"
val accompanistVersion = "0.24.10-beta"
val composeDestinationsVersion = "1.5.12-beta"

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization") version "1.6.21"
    id("com.google.devtools.ksp") version "1.6.21-1.0.5"
    kotlin("kapt")
}

android {
    namespace = "com.hyperion"
    compileSdk = 32

    defaultConfig {
        applicationId = "com.hyperion"
        minSdk = 24
        targetSdk = 32
        versionCode = 1
        versionName = "0.0.1"

        vectorDrawables.useSupportLibrary = true
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

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = composeVersion

    applicationVariants.all {
        kotlin.sourceSets.all {
            kotlin.srcDir("build/generated/ksp/$name/kotlin")
        }
    }
}

dependencies {
    // core dependencies
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.core:core-splashscreen:1.0.0-rc01")

    // compose dependencies
    implementation("androidx.compose.ui:ui:${composeVersion}")
    implementation("androidx.compose.ui:ui-tooling:${composeVersion}")
    implementation("androidx.compose.material3:material3:1.0.0-alpha13")
    implementation("androidx.compose.material:material-icons-extended:${composeVersion}")
    implementation("androidx.paging:paging-compose:1.0.0-alpha15")

    // accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-swiperefresh:$accompanistVersion")

    // compose destinations
    implementation("io.github.raamcosta.compose-destinations:animations-core:$composeDestinationsVersion")
    ksp("io.github.raamcosta.compose-destinations:ksp:$composeDestinationsVersion")

    // other dependencies
    implementation("io.coil-kt:coil-compose:2.1.0")
    implementation("com.google.android.exoplayer:exoplayer:2.17.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.17.1")

    // ktor
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-encoding:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // hilt
    implementation("com.google.dagger:hilt-android:2.42")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.42")

    // Google & Youtube auth
    implementation("com.google.android.gms:play-services-auth:20.2.0")
    implementation("com.google.api-client:google-api-client-android:1.35.1") {
        exclude("org.apache.httpcomponents")
    }
}