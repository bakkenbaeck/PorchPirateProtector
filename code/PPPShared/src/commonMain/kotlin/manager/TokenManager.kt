package no.bakkenbaeck.pppshared.manager

import no.bakkenbaeck.pppshared.model.UserToken

object TokenManager {

    private var token: UserToken? = null

    fun currentToken(): UserToken? {
        // TODO: Fetch from secure storage
        return token
    }

    fun storeToken(token: UserToken) {
        // TODO: Store securely
        this.token = token
    }

    fun clearToken() {
        // TODO: Clear from secure storage
        token = null
    }
}