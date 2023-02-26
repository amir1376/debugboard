package ir.amirab.debugboard.core.plugins

import kotlinx.coroutines.flow.*
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties


abstract class Watchable<T>(
    val name: String,
) {
    abstract fun watchAsFlow(): Flow<T>
}

class FlowWatchable<T>(
    name: String,
    private val _flow: Flow<T>
) : Watchable<T>(name) {
    override fun watchAsFlow() = _flow
}

class VariableWatcher {
    val watchables = MutableStateFlow(emptyList<Watchable<*>>())

    fun addWatch(watchable: Watchable<*>) {
        watchables.value = watchables.value + watchable
    }

    fun removeWatch(watchable: Watchable<*>) {
        watchables.value = watchables.value - watchable
    }

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


    val watcherItems: Flow<List<WatcherItem<*>>> = watchables.flatMapLatest { watchableList ->
        mapWatchableToWatcher(watchableList)
    }

}


fun <Receiver> resolveWatcherFor(receiver: Receiver, prop: KProperty1<Receiver, *>): WatcherItem<Any?> {
    return WatcherItem(
        prop.invoke(receiver), prop.name
    )
}

val ignoreFQN = listOf(
    "kotlin.",
    "java.util."

)

data class WatcherItem<T>(
    val value: T,
    val name: String
) {

    val shortenType by lazy {
        val t = getType()
        ignoreFQN.find {
            t.startsWith(it)
        }?.let {
            t.substring(it.length)
        } ?: t
    }

    fun getType(): String {
        return value?.let {
            it::class.qualifiedName
        } ?: "null"
    }

    val shortInfo: String
        get() {
            return value.toString()
        }

    val children: List<WatcherItem<*>> by lazy {
        when (value) {
            is List<*> -> {
                value.mapIndexed { index, v ->
                    WatcherItem(v, "[$index]")
                }
            }

            is Map<*, *> -> {
                value.entries.map { entry ->
                    WatcherItem(entry.value, "[${entry.key}]")
                }
            }

            is Any -> value.javaClass.kotlin.memberProperties.map {
                resolveWatcherFor(value, it)
            }

            else -> emptyList()
        }
    }

}

