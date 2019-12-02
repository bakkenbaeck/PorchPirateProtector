package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.api.Api
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.model.UserCredentials
import no.bakkenbaeck.pppshared.validator.InputValidator
import no.bakkenbaeck.pppshared.validator.ValidationResult
import kotlin.properties.Delegates

class CreateAccountPresenter: BaseCoroutinePresenter() {

    data class CreateAccountViewModel(
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
                         confirmPassword: String?): CreateAccountViewModel {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        val confirmPasswordError = validateConfirmPassword(password, confirmPassword)

        val enableButton = (
                emailError == null
                && passwordError == null
                && confirmPasswordError == null
        )

        return CreateAccountViewModel(
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            submitButtonEnabled = enableButton
        )
    }

    suspend fun createAccountAsync(email: String?,
                                   password: String?,
                                   confirmPassword: String?,
                                   initialViewModelHandler: (CreateAccountViewModel) -> Unit,
                                   secureStorage: SecureStorage): CreateAccountViewModel  {
        val validationCheckViewModel = validateAllInput(email, password, confirmPassword)
        if (!validationCheckViewModel.submitButtonEnabled) {
            return validationCheckViewModel
        }

        initialViewModelHandler(
            CreateAccountViewModel(
                submitButtonEnabled = false,
                indicatorAnimating = true
            )
        )

        // If input is valid, these will not be null.
        val creds = UserCredentials(email!!, password!!)

        return try {
            val token = api.createAccount(creds)
            secureStorage.storeTokenString(token.token)
            CreateAccountViewModel(
                accountCreated = true
            )
        } catch (exception: Exception) {
            CreateAccountViewModel(
                apiErrorMessage = exception.message,
                submitButtonEnabled = true
            )
        }
    }

    fun createAccount(email: String?,
                      password: String?,
                      confirmPassword: String?,
                      initialViewModelHandler: (CreateAccountViewModel) -> Unit,
                      secureStorage: SecureStorage,
                      completion: (CreateAccountViewModel) -> Unit) {
        launch {
            val viewModel = createAccountAsync(
                email,
                password,
                confirmPassword,
                initialViewModelHandler,
                secureStorage
            )
            completion(viewModel)
        }
    }
}