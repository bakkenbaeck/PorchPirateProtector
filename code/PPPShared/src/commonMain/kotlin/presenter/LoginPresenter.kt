package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.api.Api
import no.bakkenbaeck.pppshared.manager.TokenManager
import no.bakkenbaeck.pppshared.model.UserCredentials
import no.bakkenbaeck.pppshared.validator.InputValidator
import no.bakkenbaeck.pppshared.validator.ValidationResult
import no.bakkenbaeck.pppshared.view.LoginView
import kotlin.properties.Delegates

class LoginPresenter(
    val view: LoginView
): BaseCoroutinePresenter(
    errorHandler = view
) {

    /// Any error which has occurred in validating the user's email address.
    private var emailError: String? by Delegates.observable<String?>(null) { _, _, newValue ->
        view.emailErrorUpdated(newValue)
    }

    /// Any error which has occurred in validating the user's password.
    private var passwordError: String? by Delegates.observable<String?>(null) { _, _, newValue ->
        view.passwordErrorUpdated(newValue)
    }

    fun validateInput() {
        val emailResult = InputValidator.validateIsEmail(view.email, "email")
        when (emailResult) {
            is ValidationResult.Invalid -> emailError = emailResult.reason
            is ValidationResult.Valid -> emailError = null
        }

        val passwordResult = InputValidator.validateInputAtLeastLength(6, view.password, "password")

        when (passwordResult) {
            is ValidationResult.Invalid -> passwordError = passwordResult.reason
            is ValidationResult.Valid -> passwordError = null
        }
    }

    fun isCurrentInputValid(): Boolean {
        return emailError == null
                && passwordError == null
    }

    fun login() {
        if (!isCurrentInputValid()) {
            return
        }

        val email = view.email
        val password = view.password

        if (email != null && password != null) {
            val creds = UserCredentials(email, password)
            view.startLoadingIndicator()
            launch {
                try {
                    val token = Api.login(creds)
                    TokenManager.storeToken(token)
                } catch (exception: Exception) {
                    println("ERROR: ${exception.message}")
                }
            }.invokeOnCompletion {
                view.stopLoadingIndicator()
                TokenManager.currentToken()?.let {
                    view.loginSucceeded()
                }
            }
        } else {
            validateInput()
        }
    }
}