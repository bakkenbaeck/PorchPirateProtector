package no.bakkenbaeck.porchpirateprotector

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import no.bakkenbaeck.porchpirateprotector.activity.MainActivity
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.porchpirateprotector.manager.SharedPreferencesManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class KeyStoreManagerTest {

    @Test
    fun encryptingAndDecryptingToken() {
        val scenario = launch<MainActivity>(MainActivity::class.java)
        scenario.onActivity { activity ->
            val manager = KeyStoreManager(activity)
            manager.clearTokenString()

            val fakeToken = "I am a FAKE TOKEN"
            manager.storeTokenString(fakeToken)

            // Did the token get encrypted and stored in fake preferences?
            val prefsManager = SharedPreferencesManager(activity)
            val storedString = prefsManager.retrieveString(SharedPreferencesManager.KeyName.EncryptedToken)
            assertNotNull(storedString)
            assertNotEquals(fakeToken, storedString)

            // When we attempt to fetch the token from the manager, is it what it should be?
            val fetchedToken = manager.fetchTokenString()
            assertNotNull(fetchedToken)
            assertEquals(fakeToken, fetchedToken)

            // When we clear the token, does it actually go away?
            manager.clearTokenString()
            val refetchedToken = manager.fetchTokenString()
            assertNull(refetchedToken)
        }
    }
}