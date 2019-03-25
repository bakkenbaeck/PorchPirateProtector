package no.bakkenbaeck.porchpirateprotector.manager

import android.content.Context
import android.content.SharedPreferences
import no.bakkenbaeck.pppshared.extension.removeIfPresent
import no.bakkenbaeck.pppshared.interfaces.InsecureStorage

class SharedPreferencesManager(
    private val context: Context
): InsecureStorage {

    enum class KeyName {
        Test,
        IPAddresses,
        EncryptedToken;
    }

    private val prefsName = "PPPAndroid"

    private fun getPreferences(): SharedPreferences {
        return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    private fun getEditor(): SharedPreferences.Editor {
        return getPreferences().edit()
    }

    fun clearValue(forKey: KeyName) {
        val editor = getEditor()
        editor.remove(forKey.name)
        editor.commit()
    }

    fun storeString(string: String, forKey: KeyName) {
        val editor = getEditor()
        editor.putString(forKey.name, string)
        editor.commit()
    }

    fun retrieveString(forKey: KeyName): String? {
        val prefs = getPreferences()
        return prefs.getString(forKey.name, null)
    }

    fun storeStringList(forKey: KeyName, list: List<String>) {
        val editor = getEditor()
        editor.putStringSet(forKey.toString(), list.toMutableSet())
        editor.commit()
    }

    fun retrieveStringList(forKey: KeyName): List<String>? {
        val prefs = getPreferences()
        return prefs.getStringSet(forKey.toString(), null)?.let { stringSet ->
            stringSet.toList()
        }
    }

    // INSECURE STORAGE

    override fun storeIPAddresses(list: List<String>) {
        storeStringList(KeyName.IPAddresses, list)
    }

    override fun loadIPAddresses(): List<String>? {
        return retrieveStringList(KeyName.IPAddresses)
    }

    override fun removeIPAddress(address: String) {
        loadIPAddresses()?.let { initialAddresses ->
            val updated = initialAddresses.removeIfPresent(address)
            storeIPAddresses(updated)
        }
    }

    override fun clearIPAddresses() {
        clearValue(KeyName.IPAddresses)
    }

}