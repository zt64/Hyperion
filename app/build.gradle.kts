plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "1.7.0-1.0.6"
    kotlin("plugin.serialization") version "1.6.21"
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
    composeOptions.kotlinCompilerExtensionVersion = "1.2.0"

    applicationVariants.all {
        kotlin.sourceSets.all {
            kotlin.srcDir("build/generated/ksp/$name/kotlin")
        }
    }
}

dependencies {
    // AndroidX core
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.core:core-splashscreen:1.0.0-rc01")

    // AndroidX paging
    implementation("androidx.paging:paging-compose:1.0.0-alpha15")

    // Koin
    val koinVersion = "3.2.0"
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")

    // Compose
    val composeVersion = "1.3.0-alpha01"
    implementation("androidx.compose.ui:ui:${composeVersion}")
    debugImplementation("androidx.compose.ui:ui-tooling:${composeVersion}")
    implementation("androidx.compose.material3:material3:1.0.0-alpha14")
    implementation("androidx.compose.material:material-icons-extended:${composeVersion}")

    // Accompanist
    val accompanistVersion = "0.24.13-rc"
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-swiperefresh:$accompanistVersion")

    // Compose destinations
    val composeDestinationsVersion = "1.6.13-beta"
    implementation("io.github.raamcosta.compose-destinations:animations-core:$composeDestinationsVersion")
    ksp("io.github.raamcosta.compose-destinations:ksp:$composeDestinationsVersion")

    // Coil
    implementation("io.coil-kt:coil-compose:2.1.0")

    // KotlinX
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // Media3
    val media3Version = "1.0.0-beta01"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")

    // Ktor
    val ktorVersion = "2.0.3"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-encoding:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Google & Youtube auth
    implementation("com.google.android.gms:play-services-auth:20.2.0")
    implementation("com.google.api-client:google-api-client-android:2.0.0") {
        exclude("org.apache.httpcomponents")
    }
}