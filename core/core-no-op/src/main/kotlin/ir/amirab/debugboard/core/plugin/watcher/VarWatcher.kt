package ir.amirab.debugboard.core.plugin.watcher

import kotlinx.coroutines.flow.*


class VariableWatcher:IVariableWatcher {
    override val watchables: MutableStateFlow<List<Watchable<*>>> = MutableStateFlow(emptyList<Watchable<*>>())

    override fun addWatch(watchable: Watchable<*>) {
    }

    override fun removeWatch(watchable: Watchable<*>) {
        watchable.onWatchStopped()
    }

    override val watcherItems: Flow<List<WatcherItem<*>>> = emptyFlow()

}
