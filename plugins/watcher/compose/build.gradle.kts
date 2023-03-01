plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}
dependencies {
    implementation(project(":core"))
    implementation(compose.runtime)
}