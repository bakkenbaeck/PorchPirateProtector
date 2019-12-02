package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.platformRunBlocking
import no.bakkenbaeck.pppshared.mock.*
import no.bakkenbaeck.pppshared.validator.*
import kotlin.test.*

class CreateAccountTests {

    val presenter = CreateAccountPresenter()
    val secureStorage = MockSecureStorage()

    @Test
    fun properErrorsShownForAllValuesNull() {
        val viewModel = presenter.validateAllInput(
            email = null,
            password = null,
            confirmPassword = null
        )

        assertFalse(viewModel.submitButtonEnabled)
        assertFalse(viewModel.accountCreated)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.apiErrorMessage)

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewModel.emailError)

        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")
        assertEquals(expectedPasswordError.reason, viewModel.passwordError)

        val initialConfirmPasswordError = ValidationResult.Invalid.WasNull("confirm password")
        assertEquals(initialConfirmPasswordError.reason, viewModel.confirmPasswordError)
    }


    @Test
    fun properErrorsShownForOnlyConformPassword() {
        val viewModel = presenter.validateAllInput(
            email = null,
            password = null,
            confirmPassword = "aaaa"
        )

        assertFalse(viewModel.submitButtonEnabled)
        assertFalse(viewModel.accountCreated)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.apiErrorMessage)

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewModel.emailError)

        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")
        assertEquals(expectedPasswordError.reason, viewModel.passwordError)

        val secondConfirmError = ValidationResult.Invalid.WasNull("password")
        assertEquals(secondConfirmError.reason, viewModel.confirmPasswordError)
    }

    @Test
    fun properErrorsShowForMismatchedPasswordsAndNoEmail() {
        val viewModel = presenter.validateAllInput(
            email = null,
            password = "aaaaaa",
            confirmPassword = "aaaa"
        )

        assertFalse(viewModel.submitButtonEnabled)
        assertFalse(viewModel.accountCreated)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.apiErrorMessage)

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewModel.emailError)

        assertNull(viewModel.passwordError)

        val thirdConfirmError = ValidationResult.Invalid.InputMismatch("password", "confirm password")
        assertEquals(thirdConfirmError.reason, viewModel.confirmPasswordError)
    }

    @Test
    fun properErrorsShowForMismatchedPasswordsWithRealEmail() {
        val viewModel = presenter.validateAllInput(
            email = "not@real.biz",
            password = "aaaaaa",
            confirmPassword = "aaaa"
        )


        assertFalse(viewModel.submitButtonEnabled)
        assertFalse(viewModel.accountCreated)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.apiErrorMessage)

        assertNull(viewModel.emailError)
        assertNull(viewModel.passwordError)

        val thirdConfirmError =
            ValidationResult.Invalid.InputMismatch("password", "confirm password")
        assertEquals(thirdConfirmError.reason, viewModel.confirmPasswordError)
    }

    @Test
    fun noErrorsForValidInput() {
        val viewModel = presenter.validateAllInput(
            email = "not@real.biz",
            password = "aaaaaa",
            confirmPassword = "aaaaaa"
        )

        assertTrue(viewModel.submitButtonEnabled)

        assertFalse(viewModel.accountCreated)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.apiErrorMessage)

        assertNull(viewModel.emailError)
        assertNull(viewModel.passwordError)
        assertNull(viewModel.confirmPasswordError)
    }

    @Test
    fun attemptingToSubmitWithInvalidInputTriggersErrorsAndFails() = platformRunBlocking {
        val viewModel = presenter.createAccountAsync(
            email = null,
            password = null,
            confirmPassword = null,
            initialViewModelHandler = { _ ->
                fail("This shouldn't get hit since input is invalid")
            },
            secureStorage = secureStorage
        )

        assertFalse(viewModel.submitButtonEnabled)
        assertFalse(viewModel.indicatorAnimating)
        assertFalse(viewModel.accountCreated)
        assertNull(viewModel.apiErrorMessage)

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewModel.emailError)

        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")
        assertEquals(expectedPasswordError.reason, viewModel.passwordError)

        val expectedConfirmPasswordError = ValidationResult.Invalid.WasNull("confirm password")
        assertEquals(expectedConfirmPasswordError.reason, viewModel.confirmPasswordError)
    }

    @Test
    fun attemptingToCreateAccountWithValidCredsSucceeds() = platformRunBlocking {
        presenter.api.client = MockNetworkClient()

        var wasInitialHit = false
        val viewModel = presenter.createAccountAsync(
            email =  MockNetworkClient.validUsername,
            password = "password",
            confirmPassword = "password",
            initialViewModelHandler = { initialViewModel ->
                wasInitialHit = true
                assertTrue(initialViewModel.indicatorAnimating)
                assertFalse(initialViewModel.submitButtonEnabled)
                assertFalse(initialViewModel.accountCreated)
                assertNull(initialViewModel.apiErrorMessage)
                assertNull(initialViewModel.emailError)
                assertNull(initialViewModel.passwordError)
                assertNull(initialViewModel.confirmPasswordError)
            },
            secureStorage = secureStorage
        )

        assertTrue(wasInitialHit)


        assertTrue(viewModel.accountCreated)
        assertFalse(viewModel.indicatorAnimating)
        assertFalse(viewModel.submitButtonEnabled)
        assertNull(viewModel.apiErrorMessage)
        assertNull(viewModel.emailError)
        assertNull(viewModel.passwordError)
        assertNull(viewModel.confirmPasswordError)

        assertNotNull(secureStorage.tokenString)
        assertEquals(MockNetworkClient.mockToken, secureStorage.tokenString)
    }

    @Test
    fun attemptingToCreateAccountWithExistingCredsFails() = platformRunBlocking {
        presenter.api.client = MockNetworkClient()

        var wasInitialHit = false
        val viewModel = presenter.createAccountAsync(
            email =  MockNetworkClient.takenUsername,
            password = "password",
            confirmPassword = "password",
            initialViewModelHandler = { initialViewModel ->
                wasInitialHit = true
                assertTrue(initialViewModel.indicatorAnimating)
                assertFalse(initialViewModel.submitButtonEnabled)
                assertFalse(initialViewModel.accountCreated)
                assertNull(initialViewModel.apiErrorMessage)
                assertNull(initialViewModel.emailError)
                assertNull(initialViewModel.passwordError)
                assertNull(initialViewModel.confirmPasswordError)
            },
            secureStorage = secureStorage
        )

        assertTrue(wasInitialHit)

        assertTrue(viewModel.submitButtonEnabled)
        assertFalse(viewModel.accountCreated)
        assertFalse(viewModel.indicatorAnimating)
        assertEquals("Account already exists", viewModel.apiErrorMessage)
        assertNull(viewModel.emailError)
        assertNull(viewModel.passwordError)
        assertNull(viewModel.confirmPasswordError)

        assertNull(secureStorage.tokenString)
    }
}