package no.bakkenbaeck.pppshared.controller

import no.bakkenbaeck.pppshared.model.UserToken

object TokenManager {

    fun currentToken(): UserToken? {
        // TODO: Fetch from secure storage
        return null
    }

    fun storeToken(token: UserToken) {
        // TODO: Store securely
    }

    fun clearToken() {
        // TODO: Clear from secure storage
    }
}