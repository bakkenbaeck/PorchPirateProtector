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
        val viewModel = presenter.validateAllInput(
            email = null,
            password = null
        )

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewModel.emailError)

        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")
        assertEquals(expectedPasswordError.reason, viewModel.passwordError)

        assertFalse(viewModel.submitButtonEnabled)
        assertFalse(viewModel.loginSucceeded)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.apiError)
    }

    @Test
    fun validationSetsProperErrorsForNullEmailAndValidPassword() {
        val viewModel = presenter.validateAllInput(
            email = null,
            password = "aaaaaa"
        )

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewModel.emailError)

        assertNull(viewModel.passwordError)

        assertFalse(viewModel.submitButtonEnabled)
        assertFalse(viewModel.loginSucceeded)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.apiError)
    }

    @Test
    fun validationPassesForValidCreds() {
        val viewModel = presenter.validateAllInput(
            email = "not@real.biz",
            password = "aaaaaa"
        )

        assertNull(viewModel.emailError)
        assertNull(viewModel.passwordError)

        assertTrue(viewModel.submitButtonEnabled)
        assertFalse(viewModel.loginSucceeded)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.apiError)
    }

    @Test
    fun attemptingToLoginWithoutChangesTriggersErrorsAndFails() = platformRunBlocking {
        val viewModel = presenter.loginAsync(
            email = null,
            password = null,
            initialViewModelHandler = { _ ->
                fail("Should not have hit this with invalid creds")
            },
            secureStorage = storage
        )

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        assertEquals(expectedEmailError.reason, viewModel.emailError)

        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")
        assertEquals(expectedPasswordError.reason, viewModel.passwordError)

        assertFalse(viewModel.submitButtonEnabled)
        assertFalse(viewModel.loginSucceeded)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.apiError)
    }

    @Test
    fun attemptingToLoginWithValidCredsSucceeds() = platformRunBlocking {
        presenter.api.client = MockNetworkClient()

        var initialHit = false
        val viewModel = presenter.loginAsync(
            email = MockNetworkClient.validUsername,
            password = "password",
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertTrue(initialViewModel.indicatorAnimating)
                assertFalse(initialViewModel.submitButtonEnabled)
                assertNull(initialViewModel.emailError)
                assertNull(initialViewModel.passwordError)
                assertNull(initialViewModel.apiError)
                assertFalse(initialViewModel.loginSucceeded)
            },
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertTrue(viewModel.loginSucceeded)
        assertFalse(viewModel.submitButtonEnabled)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.emailError)
        assertNull(viewModel.passwordError)
        assertNull(viewModel.apiError)

        assertNotNull(storage.tokenString)
        assertEquals(MockNetworkClient.mockToken, storage.tokenString)
    }

    @Test
    fun attemptingToLoginWithInvalidCredsFails() = platformRunBlocking {
        presenter.api.client = MockNetworkClient()

        var initialHit = false
        val viewModel = presenter.loginAsync(
            email = MockNetworkClient.wrongPasswordUsername,
            password = "password",
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertTrue(initialViewModel.indicatorAnimating)
                assertFalse(initialViewModel.submitButtonEnabled)
                assertNull(initialViewModel.emailError)
                assertNull(initialViewModel.passwordError)
                assertNull(initialViewModel.apiError)
                assertFalse(initialViewModel.loginSucceeded)
            },
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertTrue(viewModel.submitButtonEnabled)
        assertFalse(viewModel.indicatorAnimating)
        assertFalse(viewModel.loginSucceeded)
        assertNull(viewModel.emailError)
        assertNull(viewModel.passwordError)
        assertEquals("Wrong password", viewModel.apiError)

        assertNull(storage.tokenString)
    }
}