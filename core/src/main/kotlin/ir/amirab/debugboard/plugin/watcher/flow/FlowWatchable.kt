package ir.amirab.debugboard.plugin.watcher.flow

import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugin.watcher.BaseWatchable
import ir.amirab.debugboard.core.plugin.watcher.RemoveWatch
import ir.amirab.debugboard.core.plugin.watcher.Watchable
import ir.amirab.debugboard.core.plugin.watcher.addWatch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(DelicateCoroutinesApi::class)
class FlowWatchable<T>(
    name: String,
    initialValue: T,
    private val flow: Flow<T>,
) : BaseWatchable<T>(name, initialValue) {
    private var job: Job? = null
    override fun onWatchStarted() {
        job?.cancel()
        job = GlobalScope.launch {
            flow.collect {
                stateFlow.value = it
            }
        }
    }

    override fun onWatchStopped() {
        job?.cancel()
    }
}

class StateFlowWatchable<T>(
    name: String,
    private val stateFlow: StateFlow<T>
) : Watchable<T>(name) {
    override fun watchAsFlow(): StateFlow<T> = stateFlow
}

fun <T> addWatch(
    name: String,
    initialValue: T,
    flow: Flow<T>,
    debugBoard: DebugBoard = DebugBoard.Default,
): RemoveWatch {
    val watchable = FlowWatchable(name, initialValue, flow)
    return addWatch(watchable, debugBoard)
}

fun <T> addWatch(
    name: String,
    stateflow: StateFlow<T>,
    debugBoard: DebugBoard = DebugBoard.Default,
): RemoveWatch {
    val watchable = StateFlowWatchable(name, stateflow)
    return addWatch(watchable, debugBoard)
}
