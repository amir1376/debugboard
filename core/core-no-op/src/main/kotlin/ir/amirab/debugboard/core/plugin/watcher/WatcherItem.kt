package ir.amirab.debugboard.core.plugin.watcher

data class WatcherItem<T>(
    override val value: T,
    override val name: String
) : IWatcherItem<T> {

    override val shortenType get() = ""

    override fun getType(): String {
        return ""
    }

    override val shortInfo: String
        get() {
            return value.toString()
        }

    override val children: List<WatcherItem<*>> get() = emptyList()
}