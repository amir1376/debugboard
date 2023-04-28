package ir.amirab.debugboard.core

import ir.amirab.debugboard.core.plugin.logger.ILogger
import ir.amirab.debugboard.core.plugin.network.INetworkMonitor
import ir.amirab.debugboard.core.plugin.watcher.IVariableWatcher

interface IDebugBoard {
    val variableWatcher: IVariableWatcher
    val logger: ILogger
    val networkMonitor: INetworkMonitor
}