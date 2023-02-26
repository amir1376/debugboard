plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization").version("1.8.0")
}

dependencies {
    implementation(libs.kotlin.coroutines.core)
    implementation(project(":core"))
    implementation(project(":ui:embedded"))
    implementation(project(":backend"))
    implementation(compose.desktop.currentOs)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.client.loging)
    implementation(libs.ktor.serialization.kotlinxJson)
    implementation("io.ktor:ktor-client-logging-jvm:2.2.2")
    implementation(project(":plugins:network:ktor"))
    implementation(libs.retrofit.retrofit)
    implementation(libs.retrofit.gsonConverter)
    implementation(project(":plugins:network:okhttp"))
    val fakerVersion = "1.14.0-rc.0"
    implementation("io.github.serpro69:kotlin-faker:$fakerVersion")
}