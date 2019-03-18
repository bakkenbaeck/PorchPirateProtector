package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.view.WelcomeView

class WelcomePresenter(
    val view: WelcomeView,
    val storage: SecureStorage
): BaseCoroutinePresenter(secureStorage = storage) {

    private val loginRequired: Boolean
        get() = (optionalToken() == null)

    fun skipWelcomeIfLoggedIn() {
        if (!loginRequired) {
            view.navigateToDeviceList()
        } // else, stay here.
    }

    fun selectedLoginButton() {
        view.navigateToLogin()
    }

    fun selectedCreateAccountButton() {
        view.navigateToCreateAccount()
    }
}