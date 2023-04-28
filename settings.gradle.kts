rootProject.name = "debugbar"
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }
}

include(":core:core-common")
include("core:core")
include("core:core-no-op")

include(":backend:backend-common")
include(":backend:backend")
include(":backend:backend-no-op")

include(":backend:shared-api-models")

include(":ui:idea-plugin")
include(":ui:embedded")
include(":ui:web")

include(":plugins:network:ktor")
include(":plugins:network:ktor-no-op")
include(":plugins:network:okhttp")
include(":plugins:network:okhttp-no-op")

include(":plugins:logger:timber")
include(":plugins:logger:timber-no-op")

include(":plugins:watcher:compose")
include(":plugins:watcher:compose-no-op")

include(":samples:android")
include(":samples:desktop")
