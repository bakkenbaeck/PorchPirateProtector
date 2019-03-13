package no.bakkenbaeck.porchpirateprotector.manager

import no.bakkenbaeck.pppshared.interfaces.SecureStorage

object KeyStoreManager: SecureStorage {

    // TODO: Actually use keystore to store this.
    private var token: String? = null

    override fun storeTokenString(token: String) {
        this.token = token
    }

    override fun clearTokenString() {
        token = null
    }

    override fun fetchTokenString(): String? {
        return token
    }

}