package no.bakkenbaeck.pppshared

expect fun platformName(): String

fun createApplicationScreenMessage(): String {
    return "Kotlin rocks on ${platformName()}"
}