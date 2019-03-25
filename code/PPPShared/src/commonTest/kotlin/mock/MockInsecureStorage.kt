package no.bakkenbaeck.pppshared.mock

import no.bakkenbaeck.pppshared.interfaces.InsecureStorage

class MockInsecureStorage: InsecureStorage {
    private var localIPAddresses: List<String>? = null

    override fun storeIPAddresses(list: List<String>) {
        localIPAddresses = list
    }

    override fun loadIPAddresses(): List<String>? {
        return localIPAddresses
    }

    override fun clearIPAddresses() {
        localIPAddresses = null
    }

    override fun removeIPAddress(address: String) {
        localIPAddresses?.let {
            if (it.contains(address)) {
                val updated = it.toMutableList()
                updated.remove(address)
                storeIPAddresses(updated)
            } else {
                throw RuntimeException("Trying to remove that which ain't there!")
            }
        } ?: throw RuntimeException("Nothing to remove from!")
    }
}