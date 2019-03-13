package no.bakkenbaeck.pppshared.extension

fun <T>Collection<T>.indexOrNull(element: T): Int? {
    val index = this.indexOf(element)
    return if (index >= 0) {
        index
    } else {
        null
    }
}