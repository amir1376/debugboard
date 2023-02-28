package ir.amirab.debugboard.core

import ir.amirab.debugboard.core.plugins.Logger
import ir.amirab.debugboard.core.plugins.NetworkMonitor
import ir.amirab.debugboard.core.plugins.VariableWatcher

class DebugBoard {
    companion object {
        val Default=DebugBoard()
    }

    val variableWatcher = VariableWatcher()
    val logger = Logger()
    val networkMonitor = NetworkMonitor()
}