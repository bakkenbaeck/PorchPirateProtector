package no.bakkenbaeck.kotlinnative

expect fun platformName(): String

fun createApplicationScreenMessage(): String {
    return "Kotlin rocks on ${platformName()}"
}