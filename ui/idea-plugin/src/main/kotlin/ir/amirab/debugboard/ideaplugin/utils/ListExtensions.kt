package ir.amirab.debugboard.ideaplugin.utils

fun <T> MutableList<T>.addOrReplace(item: T, selector: (T) -> Any?) {
    val selected = selector(item)
    val index = indexOfFirst {
        selector(it) == selected
    }
    if (index == -1) {
        add(item)
    } else {
        this[index] = item
    }
}

fun <T> MutableList<T>.addOrReplace(
    newItems: List<T>,
    selector: (T) -> Any
) {
    newItems.forEach {
        this.addOrReplace(it, selector)
    }
}