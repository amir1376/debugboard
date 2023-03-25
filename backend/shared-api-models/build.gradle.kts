plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}
dependencies {
    implementation(libs.kotlin.serialization.core)
    implementation(libs.kotlin.serialization.json)
}
