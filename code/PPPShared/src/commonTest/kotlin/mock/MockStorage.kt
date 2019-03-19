package no.bakkenbaeck.pppshared

import no.bakkenbaeck.pppshared.interfaces.SecureStorage

class MockStorage: SecureStorage {

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