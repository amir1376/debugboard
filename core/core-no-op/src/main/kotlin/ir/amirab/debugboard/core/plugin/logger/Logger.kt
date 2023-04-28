package ir.amirab.debugboard.core.plugin.logger

import kotlinx.coroutines.flow.*


class Logger : ILogger {
    override val logs = MutableStateFlow<List<LogData>>(emptyList()).asStateFlow()
    override val newLogs = MutableSharedFlow<LogData>().asSharedFlow()
    override fun log(logData: LogData) {}
}