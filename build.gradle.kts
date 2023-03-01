plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    val kotlinVersion = "1.8.0"
    val agpVersion = "7.3.0"
    val composeVersion = "1.3.0"

    kotlin("jvm").version(kotlinVersion) apply false
    kotlin("multiplatform").version(kotlinVersion) apply false
    kotlin("android").version(kotlinVersion) apply false
    kotlin("plugin.serialization").version(kotlinVersion) apply false
//    id("com.android.base").version(agpVersion)  apply false
    id("com.android.application").version(agpVersion) apply false
    id("com.android.library").version(agpVersion) apply false
    id("org.jetbrains.compose").version(composeVersion) apply false
}
val projectCompileSdk by ext(33)
val projectTargetSdk by ext(33)

allprojects {
    group = "ir.amirab.debugboard"
    version = "0.2.2"
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenLocal()
    }
}
subprojects {
    afterEvaluate {
        if (this.plugins.hasPlugin("com.android.library")) {
            fun Project.android(configure: Action<com.android.build.gradle.LibraryExtension>): Unit =
                (this as ExtensionAware).extensions.configure("android", configure)
            android {
                compileSdk = projectCompileSdk
            }
        }
    }
}