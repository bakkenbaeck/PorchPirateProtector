package no.bakkenbaeck.pppshared.extension

fun <T> Collection<T>.indexOrNull(element: T): Int? {
    val index = this.indexOf(element)
    return if (index >= 0) {
        index
    } else {
        null
    }
}

fun <T> List<T>.removeIfPresent(element: T): List<T> = if (this.contains(element)) {
    val mutableCopy = this.toMutableList()
    mutableCopy.remove(element)
    mutableCopy.toList()
} else {
    this
}