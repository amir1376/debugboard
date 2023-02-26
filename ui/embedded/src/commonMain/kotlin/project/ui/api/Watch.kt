package project.ui.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import ir.amirab.debugboard.core.plugins.Watchable
import project.ui.variablewatcher.rememberVariableWatcher

@Composable
fun <T> Watch(
    watchable: Watchable<T>
) {
    val watcher = rememberVariableWatcher()
    DisposableEffect(watchable) {
        watcher.addWatch(watchable)
        onDispose {
            watcher.removeWatch(watchable)
        }
    }
}