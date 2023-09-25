android {
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                parent!!.file("proguard-rules.pro")
            )
        }
    }
}

dependencies {
    implementation(libs.compose.material3)
}