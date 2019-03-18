package no.bakkenbaeck.pppshared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

actual fun <T> platformRunBlocking(
    block: suspend CoroutineScope.() -> T
): T {
    return runBlocking { block() }
}