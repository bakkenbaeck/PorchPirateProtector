package no.bakkenbaeck.porchpirateprotector.manager

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {

    enum class KeyName {
        Test,
        EncryptedToken;
    }

    private val prefsName = "PPPAndroid"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }

    fun clearValue(forKey: KeyName, context: Context) {
        val editor = getEditor(context)
        editor.remove(forKey.name)
        editor.commit()
    }

    fun storeString(string: String, forKey: KeyName, context: Context) {
        val editor = getEditor(context)
        editor.putString(forKey.name, string)
        editor.commit()
    }

    fun retrieveString(forKey: KeyName, context: Context): String? {
        val prefs = getPreferences(context)
        return prefs.getString(forKey.name, null)
    }
}