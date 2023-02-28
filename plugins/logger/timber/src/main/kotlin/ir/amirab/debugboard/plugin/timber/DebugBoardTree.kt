package ir.amirab.debugboard.plugin.timber

import android.util.Log
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugins.LogData
import ir.amirab.debugboard.core.plugins.LogLevel
import timber.log.Timber

class DebugBoardTree(
    private val debugBoard: DebugBoard=DebugBoard.Default
) : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        debugBoard.logger.log(
            LogData(
                time = System.currentTimeMillis(),
                level = when (priority) {
                    Log.ASSERT -> LogLevel.Assert
                    Log.DEBUG -> LogLevel.Debug
                    Log.ERROR -> LogLevel.Error
                    Log.INFO -> LogLevel.Info
                    Log.VERBOSE -> LogLevel.Verbose
                    Log.WARN -> LogLevel.Warning
                    else -> return
                },
                tag = tag.orEmpty(),
                message = message.ifEmpty {
                    t?.let {
                        it.message ?: it::class.qualifiedName
                    }.orEmpty()
                }
            )
        )
    }
}