package ir.amirab.debugboard.extentions

import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugins.watchable.Watchable

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