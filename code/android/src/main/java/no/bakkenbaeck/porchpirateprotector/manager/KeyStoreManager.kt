package no.bakkenbaeck.porchpirateprotector.manager

import android.content.Context
import android.util.Base64
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import java.security.*
import javax.crypto.Cipher


// Largely ganked from https://github.com/temyco/security-workshop-sample/blob/master/app/src/stages/stage1/level1/java/co/temy/securitysample/encryption/KeyStoreWrapper.kt
class KeyStoreManager(
    private val context: Context
): SecureStorage {

    private val sharedPrefs: SharedPreferencesManager

    init {
        sharedPrefs = SharedPreferencesManager(context)
    }


    private val keystoreName = "AndroidKeyStore"
    private val masterKeyName = "MASTER_KEY"

    private val cipher: Cipher

    private val keystore by lazy {
        val store = KeyStore.getInstance(keystoreName)
        store.load(null)
        store
    }

    private val masterKey: KeyPair?
        get() = getAndroidKeyStoreAsymmetricKeyPair(masterKeyName)


    init {
        if (masterKey == null) {
            createAndroidKeyStoreAsymmetricKey(masterKeyName)
        }
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    }

    // KEYSTORE STUFF

    private fun getAndroidKeyStoreAsymmetricKeyPair(alias: String): KeyPair? {
        val privateKey = keystore.getKey(alias, null) as PrivateKey?
        val publicKey = keystore.getCertificate(alias)?.publicKey

        return if (privateKey != null && publicKey != null) {
            KeyPair(publicKey, privateKey)
        } else {
            null
        }
    }

    private fun createAndroidKeyStoreAsymmetricKey(alias: String): KeyPair {
        val generator = KeyPairGenerator.getInstance("RSA", keystoreName)

        initGeneratorWithKeyGenParameterSpec(generator, alias)

        // Generates Key with given spec and saves it to the KeyStore
        return generator.generateKeyPair()
    }

    private fun initGeneratorWithKeyGenParameterSpec(generator: KeyPairGenerator, alias: String) {
        val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
        generator.initialize(builder.build())
    }

    // CIPHER ENCRYPTION

    private fun encrypt(data: String, key: Key?): String {
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val bytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun decrypt(data: String, key: Key?): String {
        cipher.init(Cipher.DECRYPT_MODE, key)
        val encryptedData = Base64.decode(data, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }

    // SECURE STORAGE

    override fun storeTokenString(token: String) {
        val encrypted = encrypt(token, masterKey?.public)
        sharedPrefs.storeString(
            encrypted,
            SharedPreferencesManager.KeyName.EncryptedToken
        )
    }

    override fun clearTokenString() {
        sharedPrefs.clearValue(SharedPreferencesManager.KeyName.EncryptedToken)
    }

    override fun fetchTokenString(): String? {
        val encrypted = sharedPrefs.retrieveString(SharedPreferencesManager.KeyName.EncryptedToken)

        return encrypted?.let {
            decrypt(it, masterKey?.private)
        }
    }
}