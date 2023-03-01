package ir.amirab.debugboard.core.plugin.watcher

import ir.amirab.debugboard.core.DebugBoard

typealias RemoveWatch = () -> Unit


fun <T> addWatch(
    watchable: Watchable<T>,
    debugBoard: DebugBoard = DebugBoard.Default,
): RemoveWatch {
    debugBoard.variableWatcher.addWatch(watchable)
    return {
        debugBoard.variableWatcher.removeWatch(watchable)
    }
}