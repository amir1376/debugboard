package ir.amirab.debugboard.core.extentions

import ir.amirab.debugboard.core.plugins.VariableWatcher
import ir.amirab.debugboard.core.plugins.Watchable

suspend fun VariableWatcher.withWatch(
    watchable: Watchable<*>,
    block: suspend () -> Unit
) {
    try {
        addWatch(watchable)
        block()
    } finally {
        removeWatch(watchable)
    }
}