package no.bakkenbaeck.pppshared.interfaces

interface SecureStorage {

    fun storeTokenString(token: String)
    fun clearTokenString()
    fun fetchTokenString(): String?
}