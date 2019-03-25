package no.bakkenbaeck.pppshared.interfaces

interface InsecureStorage {

    fun storeIPAddresses(list: List<String>)

    fun loadIPAddresses(): List<String>?

    fun removeIPAddress(address: String)

    fun clearIPAddresses()
}