package ir.amirab.debugboard.core

import ir.amirab.debugboard.core.plugin.logger.Logger
import ir.amirab.debugboard.core.plugin.network.NetworkMonitor
import ir.amirab.debugboard.core.plugin.watcher.VariableWatcher

class DebugBoard : IDebugBoard {
    companion object {
        val Default = DebugBoard()
    }

    override val variableWatcher = VariableWatcher()
    override val logger = Logger()
    override val networkMonitor = NetworkMonitor()
}