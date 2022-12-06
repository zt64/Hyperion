plugins {
    val kotlinVersion = "1.7.21"
    val agpVersion = "7.3.1"

    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("android") version kotlinVersion apply false
    id("com.android.application") version agpVersion apply false
    id("com.android.library") version agpVersion apply false
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }
}