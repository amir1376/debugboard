package ir.amirab.debugboard.plugin.watcher.period

import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugin.watcher.BaseWatchable
import ir.amirab.debugboard.core.plugin.watcher.RemoveWatch
import ir.amirab.debugboard.core.plugin.watcher.addWatch
import kotlinx.coroutines.*

private const val DefaultDuration = 500L

fun <T> addWatch(
    name: String,
    period: Long = DefaultDuration,
    debugBoard: DebugBoard = DebugBoard.Default,
    getValue: () -> T,
): RemoveWatch {
    // no op
    return {}
}