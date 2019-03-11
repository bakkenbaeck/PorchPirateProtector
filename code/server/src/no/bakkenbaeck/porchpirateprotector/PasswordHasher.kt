package no.bakkenbaeck.porchpirateprotector

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {

    fun hashAndSalt(password: String): String {
        val salt = BCrypt.gensalt()
        return BCrypt.hashpw(password, salt)
    }

    fun hashedValueMatches(password: String, hashedSalted: String): Boolean {
        return BCrypt.checkpw(password, hashedSalted)
    }
}