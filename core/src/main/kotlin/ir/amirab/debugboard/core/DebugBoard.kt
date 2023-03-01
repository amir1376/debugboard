package ir.amirab.debugboard.core

import ir.amirab.debugboard.core.plugin.logger.Logger
import ir.amirab.debugboard.core.plugin.network.NetworkMonitor
import ir.amirab.debugboard.core.plugin.watcher.VariableWatcher

class DebugBoard {
    companion object {
        val Default=DebugBoard()
    }

    val variableWatcher = VariableWatcher()
    val logger = Logger()
    val networkMonitor = NetworkMonitor()
}