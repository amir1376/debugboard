import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugins.watchable.ObservableWatchable
import ir.amirab.debugboard.extentions.addWatch

@Composable
fun <T> AddWatch(
    name: String,
    value: T,
    debugBoard: DebugBoard = DebugBoard.Default
) {
    val watchable = remember(name) {
        ObservableWatchable(name, value)
    }
    DisposableEffect(name) {
        val dispose = addWatch(watchable,debugBoard)
        onDispose { dispose() }
    }
    LaunchedEffect(name, value) {
        watchable.update(value)
    }
}
