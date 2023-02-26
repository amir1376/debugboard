rootProject.name = "debugbar"
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}

include(":core")
include(":backend")
include(":ui:embedded")
include(":ui:web")
include(":plugins:network:ktor")
include(":plugins:network:okhttp")
include(":plugins:logger:timber")

include(":samples:android")
include(":samples:desktop")
