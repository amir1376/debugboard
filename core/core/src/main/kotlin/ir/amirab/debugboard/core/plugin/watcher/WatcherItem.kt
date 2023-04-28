package ir.amirab.debugboard.core.plugin.watcher

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

data class WatcherItem<T>(
    override val value: T,
    override val name: String
) :IWatcherItem<T>{

    override val shortenType by lazy {
        val t = getType()
        ignoreFQN.find {
            t.startsWith(it)
        }?.let {
            t.substring(it.length)
        } ?: t
    }

    override fun getType(): String {
        return value?.let {
            it::class.qualifiedName
        } ?: "null"
    }

    override val shortInfo: String
        get() {
            return value.toString()
        }

    override val children: List<WatcherItem<*>> by lazy {
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
private fun <Receiver> resolveWatcherFor(receiver: Receiver, prop: KProperty1<Receiver, *>): WatcherItem<Any?> {
    return WatcherItem(
        prop.invoke(receiver), prop.name
    )
}