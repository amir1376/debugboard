package ir.amirab.debugboard.plugin.watcher.period

import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugin.watcher.BaseWatchable
import kotlinx.coroutines.*

const val DefaultDuration = 500L

@OptIn(DelicateCoroutinesApi::class)
class PeriodicWatchable<T>(
    name: String,
    private val getValue: () -> T,
    private val period: Long,
) : BaseWatchable<T>(name,getValue()) {
    var job:Job?=null
    override fun onWatchStarted() {
        job?.cancel()
        job=GlobalScope.launch {
            while (isActive){
                delay(period)
                stateFlow.value=getValue()
            }
        }
    }

    override fun onWatchStopped() {
        job?.cancel()
    }
}

fun <T> addWatch(
    name: String,
    period: Long = DefaultDuration,
    debugBoard: DebugBoard = DebugBoard.Default,
    getValue: () -> T,
) = run {
    val watchable = PeriodicWatchable(name, getValue, period)
    ir.amirab.debugboard.core.plugin.watcher.addWatch(watchable, debugBoard)
}