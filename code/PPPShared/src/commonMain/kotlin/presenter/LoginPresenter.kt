package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.model.UserCredentials
import no.bakkenbaeck.pppshared.validator.InputValidator
import no.bakkenbaeck.pppshared.validator.ValidationResult

class LoginPresenter: BaseCoroutinePresenter() {

    data class LoginViewState(
        val emailError: String? = null,
        val passwordError: String? = null,
        val submitButtonEnabled: Boolean = false,
        val apiError: String? = null,
        val indicatorAnimating: Boolean = false,
        val loginSucceeded: Boolean = false
    )

    fun validateEmail(email: String?): String? {
        val emailResult = InputValidator.validateIsEmail(email, "email")
        return when (emailResult) {
            is ValidationResult.Invalid -> emailResult.reason
            is ValidationResult.Valid -> null
        }
    }

    fun validatePassword(password: String?): String? {
        val passwordResult = InputValidator.validateInputAtLeastLength(6, password, "password")
        return when (passwordResult) {
            is ValidationResult.Invalid -> passwordResult.reason
            is ValidationResult.Valid -> null
        }
    }

    fun validateAllInput(email: String?, password: String?): LoginViewState {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)

        val enableSubmit = (emailError == null && passwordError == null)

        return LoginViewState(
            emailError = emailError,
            passwordError = passwordError,
            submitButtonEnabled = enableSubmit
        )
    }
    
    suspend fun loginAsync(email: String?,
                           password: String?,
                           initialViewStateHandler: (LoginViewState) -> Unit,
                           secureStorage: SecureStorage): LoginViewState {
        val validationViewState = validateAllInput(email, password)
        if (!validationViewState.submitButtonEnabled) {
            return validationViewState
        }

        initialViewStateHandler(
            LoginViewState(
                indicatorAnimating = true
            )
        )

        // If input is valid, these will not be null.
        val creds = UserCredentials(email!!, password!!)

        return try {
            val token = api.login(creds)
            secureStorage.storeTokenString(token.token)
            LoginViewState(
                loginSucceeded = true
            )
        } catch (exception: Exception) {
            LoginViewState(
                submitButtonEnabled = true,
                apiError = exception.message
            )
        }
    }

    fun login(email: String?,
              password: String?,
              initialViewStateHandler: (LoginViewState) -> Unit,
              secureStorage: SecureStorage,
              completion: (LoginViewState) -> Unit) {
        launch {
            val viewState = loginAsync(email, password, initialViewStateHandler, secureStorage)
            completion(viewState)
        }
    }

}