plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `maven-publish`
}
dependencies {
    implementation(libs.kotlin.serialization.core)
    implementation(libs.kotlin.serialization.json)
}
java {
    withJavadocJar()
    withSourcesJar()
}
publishing {
    publications {
        create("maven", MavenPublication::class) {
            afterEvaluate {
                from(components["java"])
            }
        }
    }
}