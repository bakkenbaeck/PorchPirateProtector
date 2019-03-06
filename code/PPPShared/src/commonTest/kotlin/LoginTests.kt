package no.bakkenbaeck.pppshared

import no.bakkenbaeck.pppshared.presenter.LoginPresenter
import no.bakkenbaeck.pppshared.view.LoginView
import no.bakkenbaeck.pppshared.validator.*
import kotlin.test.*


class LoginTests {

    class TestLoginView: LoginView {
        override var email: String? = null
        override var password: String? = null

        override fun startLoadingIndicator() {
            loadingSpinnerGoing = true
        }

        override fun stopLoadingIndicator() {
            loadingSpinnerGoing = false
        }

        override fun emailErrorUpdated(toString: String?) {
            emailError = toString
        }

        override fun passwordErrorUpdated(toString: String?) {
            passwordError = toString
        }

        override fun handleError(error: Throwable) {
            receivedError = error
        }

        override fun loginSucceeded() {
            loginHasSucceeded = true
        }


        var emailError: String? = null
        var passwordError: String? = null
        var receivedError: Throwable? = null

        var loadingSpinnerGoing = false
        var loginHasSucceeded = false
    }


    @Test
    fun testValidationSetsProperErrorsThenClearsThemAfterChangesMade() {
        val view = TestLoginView()
        val presenter = LoginPresenter(view)

        presenter.validateInput()

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")

        assertFalse(presenter.isCurrentInputValid())
        assertEquals(expectedEmailError.reason, view.emailError)
        assertEquals(expectedPasswordError.reason, view.passwordError)

        view.password = "aaaaaa"

        presenter.validateInput()

        assertFalse(presenter.isCurrentInputValid())
        assertEquals(expectedEmailError.reason, view.emailError)
        assertNull(view.passwordError)

        view.email = "not@real.biz"

        presenter.validateInput()
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
    fun attemptingToLoginWithoutChangesTriggersErrorsAndFails() {
        val view = TestLoginView()
        val presenter = LoginPresenter(view)

        val expectedEmailError = ValidationResult.Invalid.WasNull("email")
        val expectedPasswordError = ValidationResult.Invalid.WasNull("password")

        presenter.login()

        assertFalse(view.loadingSpinnerGoing)
        assertFalse(view.loginHasSucceeded)
        assertEquals(expectedEmailError.reason, view.emailError)
        assertEquals(expectedPasswordError.reason, view.passwordError)
    }
}