package no.bakkenbaeck.pppshared.manager

import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.model.UserToken

object TokenManager {

    fun currentToken(storage: SecureStorage): UserToken? {
        storage.fetchTokenString()?.let {
            return UserToken(it)
        } ?: return null
    }

    fun storeToken(token: UserToken, storage: SecureStorage) {
        storage.storeTokenString(token.token)
    }

    fun clearToken(storage: SecureStorage) {
        storage.clearTokenString()
    }
}