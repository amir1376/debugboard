plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}
android {
    namespace = "ir.amirab.debugboard.plugin.watcher.compose"
}

kotlin {
        android {
            publishLibraryVariants("release")
        }
        jvm()
    sourceSets {
        named("commonMain"){
            dependencies {
                implementation(project(":core"))
                implementation(compose.runtime)
            }
        }
    }
}