package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.platformRunBlocking
import no.bakkenbaeck.pppshared.validator.*
import no.bakkenbaeck.pppshared.mock.*
import kotlin.test.*

class LoginTests {
    val storage = MockSecureStorage()
    val presenter = LoginPresenter()

    @Test
    fun validationSetsProperErrorsForNullInput() {
        val viewState = presenter.validateAllInput(
            email = null,
            password = null
        )

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewState.emailError)

        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")
        assertEquals(expectedPasswordError.reason, viewState.passwordError)

        assertFalse(viewState.submitButtonEnabled)
        assertFalse(viewState.loginSucceeded)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.apiError)
    }

    @Test
    fun validationSetsProperErrorsForNullEmailAndValidPassword() {
        val viewState = presenter.validateAllInput(
            email = null,
            password = "aaaaaa"
        )

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewState.emailError)

        assertNull(viewState.passwordError)

        assertFalse(viewState.submitButtonEnabled)
        assertFalse(viewState.loginSucceeded)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.apiError)
    }

    @Test
    fun validationPassesForValidCreds() {
        val viewState = presenter.validateAllInput(
            email = "not@real.biz",
            password = "aaaaaa"
        )

        assertNull(viewState.emailError)
        assertNull(viewState.passwordError)

        assertTrue(viewState.submitButtonEnabled)
        assertFalse(viewState.loginSucceeded)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.apiError)
    }

    @Test
    fun attemptingToLoginWithoutChangesTriggersErrorsAndFails() = platformRunBlocking {
        val viewState = presenter.loginAsync(
            email = null,
            password = null,
            initialViewStateHandler = { _ ->
                fail("Should not have hit this with invalid creds")
            },
            secureStorage = storage
        )

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewState.emailError)

        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")
        assertEquals(expectedPasswordError.reason, viewState.passwordError)

        assertFalse(viewState.submitButtonEnabled)
        assertFalse(viewState.loginSucceeded)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.apiError)
    }

    @Test
    fun attemptingToLoginWithValidCredsSucceeds() = platformRunBlocking {
        presenter.api.client = MockNetworkClient()

        var initialHit = false
        val viewState = presenter.loginAsync(
            email = MockNetworkClient.validUsername,
            password = "password",
            initialViewStateHandler = { initialViewState ->
                initialHit = true
                assertTrue(initialViewState.indicatorAnimating)
                assertFalse(initialViewState.submitButtonEnabled)
                assertNull(initialViewState.emailError)
                assertNull(initialViewState.passwordError)
                assertNull(initialViewState.apiError)
                assertFalse(initialViewState.loginSucceeded)
            },
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertTrue(viewState.loginSucceeded)
        assertFalse(viewState.submitButtonEnabled)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.emailError)
        assertNull(viewState.passwordError)
        assertNull(viewState.apiError)

        assertNotNull(storage.tokenString)
        assertEquals(MockNetworkClient.mockToken, storage.tokenString)
    }

    @Test
    fun attemptingToLoginWithInvalidCredsFails() = platformRunBlocking {
        presenter.api.client = MockNetworkClient()

        var initialHit = false
        val viewState = presenter.loginAsync(
            email = MockNetworkClient.wrongPasswordUsername,
            password = "password",
            initialViewStateHandler = { initialViewState ->
                initialHit = true
                assertTrue(initialViewState.indicatorAnimating)
                assertFalse(initialViewState.submitButtonEnabled)
                assertNull(initialViewState.emailError)
                assertNull(initialViewState.passwordError)
                assertNull(initialViewState.apiError)
                assertFalse(initialViewState.loginSucceeded)
            },
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertTrue(viewState.submitButtonEnabled)
        assertFalse(viewState.indicatorAnimating)
        assertFalse(viewState.loginSucceeded)
        assertNull(viewState.emailError)
        assertNull(viewState.passwordError)
        assertEquals("Wrong password", viewState.apiError)

        assertNull(storage.tokenString)
    }
}