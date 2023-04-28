package ir.amirab.debugboard.core.plugin.watcher

import kotlinx.coroutines.flow.*


interface IVariableWatcher {
    val watchables :MutableStateFlow<List<Watchable<*>>>
    fun addWatch(watchable: Watchable<*>)
    fun removeWatch(watchable: Watchable<*>)
    val watcherItems: Flow<List<IWatcherItem<*>>>
}