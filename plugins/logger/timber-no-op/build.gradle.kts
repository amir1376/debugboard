plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}
publishing {
    publications {
        create("maven", MavenPublication::class) {
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

android {
    namespace = "ir.amirab.debugboard.plugin.timber"
    defaultConfig {
        minSdk = 14
    }
}
dependencies {
    api(project(":core:core-no-op"))
    api(libs.timber)
}