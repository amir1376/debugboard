package ir.amirab.debugboard.core.plugin.watcher

interface IWatcherItem<T> {
    val value: T
    val name: String
    val shortenType: String
    fun getType(): String
    val shortInfo: String
    val children: List<IWatcherItem<*>>
}