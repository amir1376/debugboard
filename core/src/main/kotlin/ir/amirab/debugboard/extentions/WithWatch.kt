package ir.amirab.debugboard.extentions

import ir.amirab.debugboard.core.plugins.watchable.VariableWatcher
import ir.amirab.debugboard.core.plugins.watchable.Watchable

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