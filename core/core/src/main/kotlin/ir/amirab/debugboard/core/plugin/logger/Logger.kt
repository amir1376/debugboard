package ir.amirab.debugboard.core.plugin.logger

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Logger:ILogger {
    private val flow: MutableStateFlow<List<LogData>> = MutableStateFlow(emptyList())

    override val logs = flow.asStateFlow()

    override val newLogs = MutableSharedFlow<LogData>(extraBufferCapacity = 64)

    override fun log(logData: LogData) {
        flow.value = flow.value + logData
        newLogs.tryEmit(logData)
    }
}