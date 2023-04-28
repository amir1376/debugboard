plugins {
    kotlin("multiplatform")
//    id("com.android.library")
    id("org.jetbrains.compose")
}
kotlin {
    //with this targets
//    android()
    jvm()
    //with these dependencies for each source set
    sourceSets {
        commonMain {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
                implementation(project(":core:core"))
            }
        }
        named("jvmMain") {
            dependencies {
                api(compose.desktop.common)
            }
        }
    }
}
//android {
//    compileSdk = 33
//    defaultConfig {
//        minSdk = 26
//        targetSdk = 33
//    }
//}