package ir.amirab.debugboard.extentions

import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugins.watchable.BaseWatchable
import ir.amirab.debugboard.core.plugins.watchable.Watchable
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
) = run {
    val watchable = FlowWatchable(name, initialValue, flow)
    addWatch(watchable, debugBoard)
}

fun <T> addWatch(
    name: String,
    stateflow: StateFlow<T>,
    debugBoard: DebugBoard = DebugBoard.Default,
) = run {
    val watchable = StateFlowWatchable(name, stateflow)
    addWatch(watchable, debugBoard)
}
