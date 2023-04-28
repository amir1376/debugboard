package ir.amirab.debugboard.core.plugin.logger

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

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

interface ILogger {
    val logs: StateFlow<List<LogData>>
    val newLogs: SharedFlow<LogData>
    fun log(logData: LogData)
}