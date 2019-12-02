package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.interfaces.SecureStorage

class WelcomePresenter: BaseCoroutinePresenter() {

    fun skipWelcome(secureStorage: SecureStorage): Boolean {
        return (secureStorage.fetchTokenString() != null)
    }
}