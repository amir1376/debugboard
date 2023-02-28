plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    `maven-publish`
}
android {

    namespace = "ir.amirab.debugboard.plugin.watcher.compose"
}

kotlin {
    jvm()
    android {
        publishLibraryVariants("release")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                api(compose.runtime)
            }
        }
    }
}