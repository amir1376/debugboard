package ir.amirab.debugboard.core.plugin.watcher

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class Watchable<T>(
    val name: String,
) {
    abstract fun watchAsFlow(): StateFlow<T>
    open fun onWatchStarted() {}
    open fun onWatchStopped() {}
}

open class BaseWatchable<T>(
    name: String,
    initialValue: T
) : Watchable<T>(name) {
    protected val stateFlow = MutableStateFlow(initialValue)
    override fun watchAsFlow(): StateFlow<T> = stateFlow
}

open class ObservableWatchable<T>(
    name: String,
    initialValue: T
) : BaseWatchable<T>(name, initialValue) {
    fun update(value: T) {
        stateFlow.value = value
    }
}
