package no.bakkenbaeck.pppshared

import no.bakkenbaeck.pppshared.presenter.*
import no.bakkenbaeck.pppshared.view.LoginView
import no.bakkenbaeck.pppshared.validator.*
import kotlin.test.*
import kotlinx.coroutines.*
import no.bakkenbaeck.pppshared.api.Api
import no.bakkenbaeck.pppshared.manager.TokenManager
import org.junit.Before

class LoginTests {

    class TestLoginView: LoginView {

        override var email: String? = null
        override var password: String? = null

        var loadingSpinnerGoing = false

        var loadingSpinnerWasStarted = false
        override fun startLoadingIndicator() {
            loadingSpinnerGoing = true
            loadingSpinnerWasStarted = true
        }

        var loadingSpinnerWasStopped = false
        override fun stopLoadingIndicator() {
            loadingSpinnerGoing = false
            loadingSpinnerWasStopped = true
        }

        var emailError: String? = null
        override fun emailErrorUpdated(toString: String?) {
            emailError = toString
        }

        var passwordError: String? = null
        override fun passwordErrorUpdated(toString: String?) {
            passwordError = toString
        }

        var apiError: String? = null
        override fun apiErrorUpdated(toString: String?) {
            apiError = toString
        }

        var loginHasSucceeded = false
        override fun loginSucceeded() {
            loginHasSucceeded = true
        }
    }

    @Before
    fun setup() {
        TokenManager.clearToken()
    }

    @Test
    fun validationSetsProperErrorsThenClearsThemAfterChangesMade() {
        val view = TestLoginView()
        val presenter = LoginPresenter(view)

        presenter.validateAllInput()

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")

        assertFalse(presenter.isCurrentInputValid())
        assertEquals(expectedEmailError.reason, view.emailError)
        assertEquals(expectedPasswordError.reason, view.passwordError)

        view.password = "aaaaaa"

        presenter.validatePassword()

        assertFalse(presenter.isCurrentInputValid())
        assertEquals(expectedEmailError.reason, view.emailError)
        assertNull(view.passwordError)

        view.email = "not@real.biz"

        presenter.validateEmail()
        assertTrue(presenter.isCurrentInputValid())
        assertNull(view.emailError)
        assertNull(view.passwordError)
    }

    @Test
    fun checkingValidityWithoutChangesTriggersErrors() {
        val view = TestLoginView()
        val presenter = LoginPresenter(view)

        val isValid = presenter.isCurrentInputValid()

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")

        assertFalse(isValid)
        assertEquals(expectedEmailError.reason, view.emailError)
        assertEquals(expectedPasswordError.reason, view.passwordError)
    }

    @Test
    fun attemptingToLoginWithoutChangesTriggersErrorsAndFails() = runBlocking {
        val view = TestLoginView()
        val presenter = LoginPresenter(view)
        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")

        val result = presenter.loginAsync()
        assertFalse(result)

        assertFalse(presenter.isCurrentInputValid())
        // Actual attempt to login should never have been made since
        // the input was invalid
        assertFalse(view.loadingSpinnerWasStarted)
        assertFalse(view.loadingSpinnerWasStopped)
        assertFalse(view.loadingSpinnerGoing)
        assertNull(view.apiError)
        assertFalse(view.loginHasSucceeded)

        assertEquals(expectedEmailError.reason, view.emailError)
        assertEquals(expectedPasswordError.reason, view.passwordError)
    }

    @Test
    fun attemptingToLoginWithValidCredsSucceeds() = runBlocking {
        val view = TestLoginView()
        val presenter = LoginPresenter(view)
        Api.client = MockNetworkClient()

        view.email = MockNetworkClient.validUsername
        view.password = "password"

        val result = presenter.loginAsync()
        assertTrue(result)

        assertTrue(presenter.isCurrentInputValid())

        assertNull(view.emailError)
        assertNull(view.passwordError)
        assertNull(view.apiError)
        assertTrue(view.loadingSpinnerWasStarted)
        assertTrue(view.loadingSpinnerWasStopped)
        assertFalse(view.loadingSpinnerGoing)
        assertTrue(view.loginHasSucceeded)

        assertNotNull(TokenManager.currentToken())
        assertEquals(MockNetworkClient.mockToken, TokenManager.currentToken()?.token)
    }

    @Test
    fun attemptingToLoginWithInvalidCredsFails() = runBlocking {
        val view = TestLoginView()
        val presenter = LoginPresenter(view)
        Api.client = MockNetworkClient()

        view.email = MockNetworkClient.wrongPasswordUsername
        view.password = "password"

        val result = presenter.loginAsync()
        assertFalse(result)

        assertTrue(presenter.isCurrentInputValid())
        assertNull(view.emailError)
        assertNull(view.passwordError)

        // The request should have been kicked off, but should fail.
        assertTrue(view.loadingSpinnerWasStarted)
        assertTrue(view.loadingSpinnerWasStopped)
        assertFalse(view.loadingSpinnerGoing)
        assertEquals("Wrong password", view.apiError)
        assertFalse(view.loginHasSucceeded)

        assertNull(TokenManager.currentToken())
    }
}