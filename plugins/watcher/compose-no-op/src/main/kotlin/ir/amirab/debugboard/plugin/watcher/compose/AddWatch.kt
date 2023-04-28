package ir.amirab.debugboard.plugin.watcher.compose
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugin.watcher.ObservableWatchable
import ir.amirab.debugboard.core.plugin.watcher.addWatch

@Composable
fun <T> AddWatch(
    name: String,
    value: T,
    debugBoard: DebugBoard = DebugBoard.Default
) {
    // no op
}
