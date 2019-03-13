package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.*
import no.bakkenbaeck.pppshared.ApplicationDispatcher
import no.bakkenbaeck.pppshared.api.Api
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.TokenManager
import kotlin.coroutines.CoroutineContext

// Ganked from https://github.com/JetBrains/kotlinconf-app/blob/master/common/src/commonMain/kotlin/org/jetbrains/kotlinconf/presentation/CoroutinePresenter.kt

open class BaseCoroutinePresenter(
    private val mainContext: CoroutineContext = ApplicationDispatcher,
    val api: Api = Api(),
    val secureStorage: SecureStorage
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

    fun throwingToken(): String {
        TokenManager.currentToken(secureStorage)?.let {
            return it.token
        } ?: throw RuntimeException("Couldn't find token!")
    }

    fun optionalToken(): String? {
        return TokenManager.currentToken(secureStorage)?.token
    }

    open fun handleError(error: Throwable) {
        println("ERROR: $error")
    }
}