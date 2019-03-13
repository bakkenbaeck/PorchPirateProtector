package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.ApplicationDispatcher
import no.bakkenbaeck.pppshared.api.Api
import no.bakkenbaeck.pppshared.manager.TokenManager
import no.bakkenbaeck.pppshared.model.UserCredentials
import no.bakkenbaeck.pppshared.validator.InputValidator
import no.bakkenbaeck.pppshared.validator.ValidationResult
import no.bakkenbaeck.pppshared.view.LoginView
import kotlin.properties.Delegates

class  LoginPresenter(
    val view: LoginView
): BaseCoroutinePresenter() {

    /// Any error which has occurred in validating the user's email address.
    private var emailError: String? by Delegates.observable<String?>(null) { _, _, newValue ->
        view.emailErrorUpdated(newValue)
    }

    /// Any error which has occurred in validating the user's password.
    private var passwordError: String? by Delegates.observable<String?>(null) { _, _, newValue ->
        view.passwordErrorUpdated(newValue)
    }

    private var apiError: String? by Delegates.observable<String?>(null) { _, _, newValue ->
        view.apiErrorUpdated(newValue)
    }

    fun validateEmail() {
        val emailResult = InputValidator.validateIsEmail(view.email, "email")
        when (emailResult) {
            is ValidationResult.Invalid -> emailError = emailResult.reason
            is ValidationResult.Valid -> emailError = null
        }
    }

    fun validatePassword() {
        val passwordResult = InputValidator.validateInputAtLeastLength(6, view.password, "password")

        when (passwordResult) {
            is ValidationResult.Invalid -> passwordError = passwordResult.reason
            is ValidationResult.Valid -> passwordError = null
        }
    }

    fun validateAllInput() {
        validateEmail()
        validatePassword()
    }

    fun isCurrentInputValid(): Boolean {
        validateAllInput()
        return emailError == null
                && passwordError == null
    }


    suspend fun loginAsync(): Boolean {
        if (!isCurrentInputValid()) {
            return false
        }

        // If input is valid, these will not be null.
        val creds = UserCredentials(view.email!!, view.password!!)
        view.startLoadingIndicator()
        apiError = null

        var success = false
        try {
            val token = api.login(creds)
            TokenManager.storeToken(token)
            view.loginSucceeded()
            success = true
        } catch (exception: Exception) {
            apiError = exception.message
        }

        view.stopLoadingIndicator()
        return success
    }

    fun login() {
        launch {
            loginAsync()
        }
    }

    override fun handleError(error: Throwable) {
        apiError = error.message
    }
}