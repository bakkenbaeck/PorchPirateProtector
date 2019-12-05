package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.model.UserCredentials
import no.bakkenbaeck.pppshared.validator.InputValidator
import no.bakkenbaeck.pppshared.validator.ValidationResult

class CreateAccountPresenter: BaseCoroutinePresenter() {

    data class CreateAccountViewState(
        val emailError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val submitButtonEnabled: Boolean = false,
        val indicatorAnimating: Boolean = false,
        val apiErrorMessage: String? = null,
        val accountCreated: Boolean = false
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

    fun validateConfirmPassword(password: String?, confirmPassword: String?): String? {
        val confirmPasswordResult = InputValidator.validateNonNullMatches(
            "password",
            password,
            "confirm password",
            confirmPassword
        )

        return when (confirmPasswordResult) {
            is ValidationResult.Invalid -> confirmPasswordResult.reason
            is ValidationResult.Valid -> null
        }
    }

    fun validateAllInput(email: String?,
                         password: String?,
                         confirmPassword: String?): CreateAccountViewState {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        val confirmPasswordError = validateConfirmPassword(password, confirmPassword)

        val enableButton = (
                emailError == null
                && passwordError == null
                && confirmPasswordError == null
        )

        return CreateAccountViewState(
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            submitButtonEnabled = enableButton
        )
    }

    suspend fun createAccountAsync(email: String?,
                                   password: String?,
                                   confirmPassword: String?,
                                   initialViewStateHandler: (CreateAccountViewState) -> Unit,
                                   secureStorage: SecureStorage): CreateAccountViewState  {
        val validationCheckViewState = validateAllInput(email, password, confirmPassword)
        if (!validationCheckViewState.submitButtonEnabled) {
            return validationCheckViewState
        }

        initialViewStateHandler(
            CreateAccountViewState(
                submitButtonEnabled = false,
                indicatorAnimating = true
            )
        )

        // If input is valid, these will not be null.
        val creds = UserCredentials(email!!, password!!)

        return try {
            val token = api.createAccount(creds)
            secureStorage.storeTokenString(token.token)
            CreateAccountViewState(
                accountCreated = true
            )
        } catch (exception: Exception) {
            CreateAccountViewState(
                apiErrorMessage = exception.message,
                submitButtonEnabled = true
            )
        }
    }

    fun createAccount(email: String?,
                      password: String?,
                      confirmPassword: String?,
                      initialViewStateHandler: (CreateAccountViewState) -> Unit,
                      secureStorage: SecureStorage,
                      completion: (CreateAccountViewState) -> Unit) {
        launch {
            val viewState = createAccountAsync(
                email,
                password,
                confirmPassword,
                initialViewStateHandler,
                secureStorage
            )
            completion(viewState)
        }
    }
}