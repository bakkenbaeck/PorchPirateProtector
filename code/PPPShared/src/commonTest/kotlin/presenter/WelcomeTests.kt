package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.mock.*
import kotlin.test.*

class WelcomeTests {

    val presenter = WelcomePresenter()
    val storage = MockSecureStorage()

    @Test
    fun skipsWelcomeIfTokenPresent() {
        storage.tokenString = "literally anything"
        assertTrue(presenter.skipWelcome(storage))
    }

    @Test
    fun doesNotSkipWelcomeIfNoToken() {
        assertFalse(presenter.skipWelcome(storage))
    }
}