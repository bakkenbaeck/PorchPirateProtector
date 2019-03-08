package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import no.bakkenbaeck.pppshared.ApplicationDispatcher
import no.bakkenbaeck.pppshared.interfaces.ErrorHandling
import kotlin.coroutines.CoroutineContext

// Ganked from https://github.com/JetBrains/kotlinconf-app/blob/master/common/src/commonMain/kotlin/org/jetbrains/kotlinconf/presentation/CoroutinePresenter.kt
open class BaseCoroutinePresenter(
    private val mainContext: CoroutineContext = ApplicationDispatcher
): CoroutineScope {

    private val job = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    override val coroutineContext: CoroutineContext
        get() = mainContext + job + exceptionHandler

    open fun onDestroy() {
        job.cancel()
    }

    open fun handleError(error: Throwable) {
        println("ERROR: $error")
    }
}