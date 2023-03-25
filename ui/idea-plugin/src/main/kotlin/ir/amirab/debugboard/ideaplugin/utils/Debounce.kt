package ir.amirab.debugboard.ideaplugin.utils

import kotlinx.coroutines.*
import java.util.Timer
import java.util.TimerTask

fun<T> CoroutineScope.debounce(
    delay: Long=100,
    fn: (f: T) -> Unit
): (T) -> Unit {
    var job: Job? = null
    return {
        job?.cancel()
        job = launch {
            delay(delay)
            fn(it)
        }
    }
}