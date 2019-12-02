package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.model.UserCredentials
import no.bakkenbaeck.pppshared.validator.InputValidator
import no.bakkenbaeck.pppshared.validator.ValidationResult

class LoginPresenter: BaseCoroutinePresenter() {

    data class LoginViewModel(
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

    fun validateAllInput(email: String?, password: String?): LoginViewModel {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)

        val enableSubmit = (emailError == null && passwordError == null)

        return LoginViewModel(
            emailError = emailError,
            passwordError = passwordError,
            submitButtonEnabled = enableSubmit
        )
    }
    
    suspend fun loginAsync(email: String?,
                           password: String?,
                           initialViewModelHandler: (LoginViewModel) -> Unit,
                           secureStorage: SecureStorage): LoginViewModel {
        val validationViewModel = validateAllInput(email, password)
        if (!validationViewModel.submitButtonEnabled) {
            return validationViewModel
        }

        initialViewModelHandler(
            LoginViewModel(
                indicatorAnimating = true
            )
        )

        // If input is valid, these will not be null.
        val creds = UserCredentials(email!!, password!!)

        return try {
            val token = api.login(creds)
            secureStorage.storeTokenString(token.token)
            LoginViewModel(
                loginSucceeded = true
            )
        } catch (exception: Exception) {
            LoginViewModel(
                submitButtonEnabled = true,
                apiError = exception.message
            )
        }
    }

    fun login(email: String?,
              password: String?,
              initialViewModelHandler: (LoginViewModel) -> Unit,
              secureStorage: SecureStorage,
              completion: (LoginViewModel) -> Unit) {
        launch {
            val viewModel = loginAsync(email, password, initialViewModelHandler, secureStorage)
            completion(viewModel)
        }
    }

}