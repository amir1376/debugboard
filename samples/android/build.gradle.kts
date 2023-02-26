@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    kotlin("android")
}
android {
    compileSdk = 33
    defaultConfig {
        minSdk = 30
        applicationId = "ir.amirab.debugboard.sample"
        namespace = "ir.amirab.debugboard.sample"
    }
    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.timber)


    implementation(project(":core"))
    implementation(project(":plugins:network:ktor"))
    implementation(project(":plugins:logger:timber"))

    implementation(project(":backend"))
}