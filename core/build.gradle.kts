plugins {
    kotlin("jvm")
    `maven-publish`
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

kotlin {
    jvmToolchain(8)
}
dependencies {
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.reflect)
}
