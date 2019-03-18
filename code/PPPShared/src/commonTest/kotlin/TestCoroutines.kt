package no.bakkenbaeck.pppshared

import kotlinx.coroutines.CoroutineScope

expect fun <T> platformRunBlocking(
    block: suspend CoroutineScope.() -> T
) : T