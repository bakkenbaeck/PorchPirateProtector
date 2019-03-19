package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.view.WelcomeView
import no.bakkenbaeck.pppshared.mock.*
import kotlin.test.*

class WelcomeTests {

    class TestWelcomeView: WelcomeView {
        var wentToLogin = false
        override fun navigateToLogin() {
            wentToLogin = true
        }

        var wentToCreateAccount = false
        override fun navigateToCreateAccount() {
            wentToCreateAccount = true
        }

        var wentToDeviceList = false
        override fun navigateToDeviceList() {
            wentToDeviceList = true
        }
    }

    @Test
    fun skipsWelcomeIfTokenPresent() {
        val view = TestWelcomeView()
        val storage = MockStorage()
        storage.tokenString = "literally anything"

        val presenter = WelcomePresenter(view, storage)

        presenter.skipWelcomeIfLoggedIn()

        assertTrue(view.wentToDeviceList)
        assertFalse(view.wentToLogin)
        assertFalse(view.wentToCreateAccount)
    }

    @Test
    fun doesNotSkipWelcomeIfNoToken() {
        val view = TestWelcomeView()
        val presenter = WelcomePresenter(view, MockStorage())

        presenter.skipWelcomeIfLoggedIn()

        assertFalse(view.wentToDeviceList)
        assertFalse(view.wentToLogin)
        assertFalse(view.wentToCreateAccount)
    }

    @Test
    fun navigatingToLogin() {
        val view = TestWelcomeView()
        val presenter = WelcomePresenter(view, MockStorage())

        presenter.selectedLoginButton()

        assertFalse(view.wentToDeviceList)
        assertTrue(view.wentToLogin)
        assertFalse(view.wentToCreateAccount)
    }

    @Test
    fun navigatingToCreateAccount() {
        val view = TestWelcomeView()
        val presenter = WelcomePresenter(view, MockStorage())

        presenter.selectedCreateAccountButton()

        assertFalse(view.wentToDeviceList)
        assertFalse(view.wentToLogin)
        assertTrue(view.wentToCreateAccount)
    }
}