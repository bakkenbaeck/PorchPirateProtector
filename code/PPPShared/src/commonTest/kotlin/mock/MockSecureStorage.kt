package no.bakkenbaeck.pppshared.mock

import no.bakkenbaeck.pppshared.interfaces.SecureStorage

class MockSecureStorage: SecureStorage {

    var tokenString: String? = null

    override fun storeTokenString(token: String) {
        tokenString = token
    }

    override fun clearTokenString() {
        tokenString = null
    }

    override fun fetchTokenString(): String? {
        return tokenString
    }
}