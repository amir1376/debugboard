package ir.amirab.debugboard.plugin.watcher.flow

import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugin.watcher.BaseWatchable
import ir.amirab.debugboard.core.plugin.watcher.RemoveWatch
import ir.amirab.debugboard.core.plugin.watcher.Watchable
import ir.amirab.debugboard.core.plugin.watcher.addWatch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun <T> addWatch(
    name: String,
    initialValue: T,
    flow: Flow<T>,
    debugBoard: DebugBoard = DebugBoard.Default,
): RemoveWatch {
    // no op
    return { }
}

fun <T> addWatch(
    name: String,
    stateflow: StateFlow<T>,
    debugBoard: DebugBoard = DebugBoard.Default,
): RemoveWatch {
    // no op
    return { }
}
