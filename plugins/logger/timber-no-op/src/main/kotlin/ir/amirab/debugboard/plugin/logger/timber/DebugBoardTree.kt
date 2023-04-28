package ir.amirab.debugboard.plugin.logger.timber

import android.util.Log
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugin.logger.LogData
import ir.amirab.debugboard.core.plugin.logger.LogLevel
import timber.log.Timber

class DebugBoardTree(
    private val debugBoard: DebugBoard=DebugBoard.Default
) : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // no op
    }
}