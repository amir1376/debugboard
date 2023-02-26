package project

import androidx.compose.runtime.compositionLocalOf
import ir.amirab.debugboard.core.DebugBoard

val LocalDebugBoard = compositionLocalOf<DebugBoard> {
    error("project.ui.variablewatcher.getLocalDebugBoard not initialized")
}