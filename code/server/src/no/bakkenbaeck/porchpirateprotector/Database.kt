package no.bakkenbaeck.porchpirateprotector

import no.bakkenbaeck.porchpirateprotector.model.Device
import no.bakkenbaeck.porchpirateprotector.model.Devices
import no.bakkenbaeck.porchpirateprotector.model.User
import no.bakkenbaeck.porchpirateprotector.model.Users
import no.bakkenbaeck.pppshared.model.UserCredentials
import no.bakkenbaeck.pppshared.model.UserToken
import org.jetbrains.exposed.dao.*

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ServerDB {

    private val database: Database

    init {
        database = Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        transaction(database) {
            SchemaUtils.create(Users, Devices)
        }
    }

    fun createUser(credentials: UserCredentials): User {
        return transaction(database) {
            User.new {
                username = credentials.username
                saltedHashedPassword = PasswordHasher.hashAndSalt(credentials.password)
                token = UUID.randomUUID().toString()
            }
        }
    }

    fun fetchUser(email: String): User? {
        return transaction(database) {
            User
                .find { Users.username eq email }
                .limit(1)
                .firstOrNull()
        }
    }

    fun fetchUserByToken(token: String): User? {
        return transaction(database) {
            User
                .find { Users.token eq token }
                .limit(1)
                .firstOrNull()
        }
    }

    fun fetchUser(id: Int): User? {
        return transaction(database) {
            User
                .find { Users.id eq id }
                .limit(1)
                .firstOrNull()
        }
    }

    fun createDevice(address: String): Device {
        return transaction(database) {
            Device.new {
                ipAddress = address
            }
        }
    }

    fun fetchDevice(id: Int): Device? {
        return transaction(database) {
            Device
                .find { Devices.id eq id }
                .limit(1)
                .firstOrNull()
        }
    }
}