package ir.amirab.debugboard.core.plugin.logger

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class LogLevel(name: String? = null) {
    object Info : LogLevel()
    object Warning : LogLevel()
    object Debug : LogLevel()
    object Verbose : LogLevel()
    object Error : LogLevel()
    object Assert : LogLevel()


    private val logLevelName: String by lazy {
        name ?: requireNotNull(this::class.simpleName) {
            "log level name is null and you are using anonymous class!"
        }
    }

    override fun toString() = logLevelName
}

data class LogData(
    val time: Long,
    val level: LogLevel,
    val message: String,
    val tag: String,
)

class Logger {
    private val flow: MutableStateFlow<List<LogData>> = MutableStateFlow(emptyList())

    val logs = flow.asStateFlow()

    val newLogs = MutableSharedFlow<LogData>(extraBufferCapacity = 64)

    fun log(logData: LogData) {
        flow.value = flow.value + logData
        newLogs.tryEmit(logData)
    }
}