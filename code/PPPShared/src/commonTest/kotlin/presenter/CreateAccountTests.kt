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
        val viewState = presenter.validateAllInput(
            email = null,
            password = null,
            confirmPassword = null
        )

        assertFalse(viewState.submitButtonEnabled)
        assertFalse(viewState.accountCreated)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.apiErrorMessage)

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewState.emailError)

        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")
        assertEquals(expectedPasswordError.reason, viewState.passwordError)

        val initialConfirmPasswordError = ValidationResult.Invalid.WasNull("confirm password")
        assertEquals(initialConfirmPasswordError.reason, viewState.confirmPasswordError)
    }


    @Test
    fun properErrorsShownForOnlyConformPassword() {
        val viewState = presenter.validateAllInput(
            email = null,
            password = null,
            confirmPassword = "aaaa"
        )

        assertFalse(viewState.submitButtonEnabled)
        assertFalse(viewState.accountCreated)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.apiErrorMessage)

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewState.emailError)

        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")
        assertEquals(expectedPasswordError.reason, viewState.passwordError)

        val secondConfirmError = ValidationResult.Invalid.WasNull("password")
        assertEquals(secondConfirmError.reason, viewState.confirmPasswordError)
    }

    @Test
    fun properErrorsShowForMismatchedPasswordsAndNoEmail() {
        val viewState = presenter.validateAllInput(
            email = null,
            password = "aaaaaa",
            confirmPassword = "aaaa"
        )

        assertFalse(viewState.submitButtonEnabled)
        assertFalse(viewState.accountCreated)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.apiErrorMessage)

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewState.emailError)

        assertNull(viewState.passwordError)

        val thirdConfirmError = ValidationResult.Invalid.InputMismatch("password", "confirm password")
        assertEquals(thirdConfirmError.reason, viewState.confirmPasswordError)
    }

    @Test
    fun properErrorsShowForMismatchedPasswordsWithRealEmail() {
        val viewState = presenter.validateAllInput(
            email = "not@real.biz",
            password = "aaaaaa",
            confirmPassword = "aaaa"
        )

        assertFalse(viewState.submitButtonEnabled)
        assertFalse(viewState.accountCreated)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.apiErrorMessage)

        assertNull(viewState.emailError)
        assertNull(viewState.passwordError)

        val thirdConfirmError =
            ValidationResult.Invalid.InputMismatch("password", "confirm password")
        assertEquals(thirdConfirmError.reason, viewState.confirmPasswordError)
    }

    @Test
    fun noErrorsForValidInput() {
        val viewState = presenter.validateAllInput(
            email = "not@real.biz",
            password = "aaaaaa",
            confirmPassword = "aaaaaa"
        )

        assertTrue(viewState.submitButtonEnabled)

        assertFalse(viewState.accountCreated)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.apiErrorMessage)

        assertNull(viewState.emailError)
        assertNull(viewState.passwordError)
        assertNull(viewState.confirmPasswordError)
    }

    @Test
    fun attemptingToSubmitWithInvalidInputTriggersErrorsAndFails() = platformRunBlocking {
        val viewState = presenter.createAccountAsync(
            email = null,
            password = null,
            confirmPassword = null,
            initialViewStateHandler = { _ ->
                fail("This shouldn't get hit since input is invalid")
            },
            secureStorage = secureStorage
        )

        assertFalse(viewState.submitButtonEnabled)
        assertFalse(viewState.indicatorAnimating)
        assertFalse(viewState.accountCreated)
        assertNull(viewState.apiErrorMessage)

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewState.emailError)

        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")
        assertEquals(expectedPasswordError.reason, viewState.passwordError)

        val expectedConfirmPasswordError = ValidationResult.Invalid.WasNull("confirm password")
        assertEquals(expectedConfirmPasswordError.reason, viewState.confirmPasswordError)
    }

    @Test
    fun attemptingToCreateAccountWithValidCredsSucceeds() = platformRunBlocking {
        presenter.api.client = MockNetworkClient()

        var wasInitialHit = false
        val viewState = presenter.createAccountAsync(
            email =  MockNetworkClient.validUsername,
            password = "password",
            confirmPassword = "password",
            initialViewStateHandler = { initialViewState ->
                wasInitialHit = true
                assertTrue(initialViewState.indicatorAnimating)
                assertFalse(initialViewState.submitButtonEnabled)
                assertFalse(initialViewState.accountCreated)
                assertNull(initialViewState.apiErrorMessage)
                assertNull(initialViewState.emailError)
                assertNull(initialViewState.passwordError)
                assertNull(initialViewState.confirmPasswordError)
            },
            secureStorage = secureStorage
        )

        assertTrue(wasInitialHit)


        assertTrue(viewState.accountCreated)
        assertFalse(viewState.indicatorAnimating)
        assertFalse(viewState.submitButtonEnabled)
        assertNull(viewState.apiErrorMessage)
        assertNull(viewState.emailError)
        assertNull(viewState.passwordError)
        assertNull(viewState.confirmPasswordError)

        assertNotNull(secureStorage.tokenString)
        assertEquals(MockNetworkClient.mockToken, secureStorage.tokenString)
    }

    @Test
    fun attemptingToCreateAccountWithExistingCredsFails() = platformRunBlocking {
        presenter.api.client = MockNetworkClient()

        var wasInitialHit = false
        val viewState = presenter.createAccountAsync(
            email =  MockNetworkClient.takenUsername,
            password = "password",
            confirmPassword = "password",
            initialViewStateHandler = { initialViewState ->
                wasInitialHit = true
                assertTrue(initialViewState.indicatorAnimating)
                assertFalse(initialViewState.submitButtonEnabled)
                assertFalse(initialViewState.accountCreated)
                assertNull(initialViewState.apiErrorMessage)
                assertNull(initialViewState.emailError)
                assertNull(initialViewState.passwordError)
                assertNull(initialViewState.confirmPasswordError)
            },
            secureStorage = secureStorage
        )

        assertTrue(wasInitialHit)

        assertTrue(viewState.submitButtonEnabled)
        assertFalse(viewState.accountCreated)
        assertFalse(viewState.indicatorAnimating)
        assertEquals("Account already exists", viewState.apiErrorMessage)
        assertNull(viewState.emailError)
        assertNull(viewState.passwordError)
        assertNull(viewState.confirmPasswordError)

        assertNull(secureStorage.tokenString)
    }
}