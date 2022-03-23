val pagingVersion = "3.1.0"
val composeVersion = "1.1.1"
val ktorVersion = "1.6.8"
val accompanistVersion = "0.24.4-alpha"

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp") version "1.6.10-1.0.4"
    kotlin("kapt")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.hyperion"
        minSdk = 24
        targetSdk = 31
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
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }

    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = composeVersion
}

kotlin {
    sourceSets {
        debug {
            kotlin.srcDir("build/generated/ksp/debug/kotlin")
        }
        release {
            kotlin.srcDir("build/generated/ksp/release/kotlin")
        }
    }
}

dependencies {
    // core dependencies
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.core:core-splashscreen:1.0.0-beta01")

    // compose dependencies
    implementation("androidx.compose.ui:ui:${composeVersion}")
    implementation("androidx.compose.ui:ui-tooling:${composeVersion}")
    implementation("androidx.compose.material:material:${composeVersion}")
    implementation("androidx.compose.material3:material3:1.0.0-alpha07")
    implementation("androidx.compose.material:material-icons-extended:1.2.0-alpha05")
    implementation("androidx.paging:paging-compose:1.0.0-alpha14")

    // accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-swiperefresh:$accompanistVersion")

    // compose destinations
    implementation("io.github.raamcosta.compose-destinations:animations-core:1.3.5-beta")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.3.5-beta")

    // other dependencies
    implementation("io.coil-kt:coil-compose:2.0.0-rc02")
    implementation("com.google.android.exoplayer:exoplayer:2.17.1")

    // ktor
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-gson:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    // hilt
    implementation("com.google.dagger:hilt-android:2.41")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.41")

    // Google & Youtube auth
    implementation("com.google.android.gms:play-services-auth:20.1.0")
    implementation("com.google.api-client:google-api-client-android:1.33.2") {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-youtube:v3-rev20220312-1.32.1") {
        exclude("org.apache.httpcomponents")
    }
}