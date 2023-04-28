package ir.amirab.debugboard.core.plugin.watcher

import kotlinx.coroutines.flow.*


class VariableWatcher:IVariableWatcher {
    override val watchables: MutableStateFlow<List<Watchable<*>>> = MutableStateFlow(emptyList<Watchable<*>>())

    override fun addWatch(watchable: Watchable<*>) {
        watchable.onWatchStarted()
        watchables.value = watchables.value + watchable
    }

    override fun removeWatch(watchable: Watchable<*>) {
        watchable.onWatchStopped()
        watchables.value = watchables.value - watchable
    }

    var count = 0
    private fun mapWatchableToWatcher(p: List<Watchable<*>>): Flow<List<WatcherItem<*>>> {
        val flows = p.map {
            it.watchAsFlow()
        }
        val names = p.map {
            it.name
        }
        val x = combine(
            flows = flows
        ) {
            it.zip(names).map { pair: Pair<Any?, String> ->
                WatcherItem(
                    name = pair.second,
                    value = pair.first
                )
            }
        }

        return x
    }


    override val watcherItems: Flow<List<WatcherItem<*>>> = watchables.flatMapLatest { watchableList ->
        mapWatchableToWatcher(watchableList)
    }

}


val ignoreFQN = listOf(
    "kotlin.",
    "java.util."
)

