fun <T> Iterable<T>.split(predicate: (T) -> Boolean): List<List<T>> {
    val mainList = ArrayList<List<T>>()
    var subList = ArrayList<T>()
    for (item in this) {
        if (predicate(item)) {
            mainList.add(subList)
            subList = ArrayList()
        } else {
            subList.add(item)
        }
    }
    mainList.add(subList)
    return mainList
}

fun <T> Iterable<T>.split(on: T): List<List<T>> {
    return split { it == on }
}

fun <T> Iterable<T>.split(onAny: Collection<T>): List<List<T>> {
    return split { onAny.contains(it) }
}