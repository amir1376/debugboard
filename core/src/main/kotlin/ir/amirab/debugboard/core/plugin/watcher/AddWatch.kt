package ir.amirab.debugboard.core.plugin.watcher

import ir.amirab.debugboard.core.DebugBoard

typealias Dispose = () -> Unit


fun <T> addWatch(
    watchable: Watchable<T>,
    debugBoard: DebugBoard = DebugBoard.Default,
): Dispose {
    debugBoard.variableWatcher.addWatch(watchable)
    return {
        debugBoard.variableWatcher.removeWatch(watchable)
    }
}