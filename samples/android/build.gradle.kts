@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 33
    defaultConfig {
        // as we use compose this is minimum sdk that compose supports
        minSdk = 21
        applicationId = "ir.amirab.debugboard.sample"
        namespace = "ir.amirab.debugboard.sample"
    }
    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
    buildFeatures{
        compose=true
    }
    composeOptions{
        kotlinCompilerExtensionVersion= "1.4.0"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.timber)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.androidx.activity.compose)

    /**
     * this is my multimodule dependencies for debug board
     * don't copy this for yours
     * for proper dependencies please see README.md file in GitHub
     */
    implementation(project(":core"))
    implementation(project(":plugins:network:ktor"))
    implementation(project(":plugins:logger:timber"))
    implementation(project(":plugins:watcher:compose"))
    implementation(project(":backend"))
}