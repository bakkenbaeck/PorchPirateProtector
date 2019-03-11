package no.bakkenbaeck.porchpirateprotector.model

import io.ktor.auth.Principal
import no.bakkenbaeck.pppshared.model.UserToken
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Users: IntIdTable() {
    val username = varchar("username", 100) // Column<String>
    val saltedHashedPassword = varchar("salted_hashed", 100) // Column<String>
    val token = varchar("token", 100).nullable() // Column<String?>
}

class User(id: EntityID<Int>): IntEntity(id), Principal {
    companion object: IntEntityClass<User>(Users)

    var username by Users.username
    var saltedHashedPassword by Users.saltedHashedPassword
    var token by Users.token

    fun userToken(): UserToken? {
        token?.let {
            return UserToken(it)
        }

        return null
    }

    fun invalidateToken() {
        token = null
    }
}
