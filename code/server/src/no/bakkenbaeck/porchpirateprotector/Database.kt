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
        /// The IP of your MySql server.
        // If you're using docker, use `docker container inspect [container name]` to get your IP address.
        val serverIP = "172.17.0.2"

        /// The MySql username for you database.
        /// You should probably use something other than "root" for anything you're deploying.
        val databaseUsername = "root"

        /// The password for your user in the database.
        /// For the love of god, use something more secure than this.
        val databasePassword = "password"

        /// The name of the actual database you want to put data into.
        /// Make sure this database already exists before trying to run the server.
        /// > mysql -u {username} -p
        /// > [enter your password when prompted]
        /// > create table {databaseName}
        val databaseName = "ppp"

        database = Database.connect("jdbc:mysql://$serverIP:3306/$databaseName  ",
            user = databaseUsername,
            password = databasePassword,
            driver = "com.mysql.jdbc.Driver"
        )
        transaction {
            SchemaUtils.create(Users, Devices)
        }
    }

    fun createUser(credentials: UserCredentials): User {
        return transaction {
            User.new {
                username = credentials.username
                saltedHashedPassword = PasswordHasher.hashAndSalt(credentials.password)
                token = UUID.randomUUID().toString()
            }
        }
    }

    fun fetchUser(email: String): User? {
        return transaction {
            User
                .find { Users.username eq email }
                .limit(1)
                .firstOrNull()
        }
    }

    fun updateToken(user: User): User {
        return transaction {
            if (user.token == null) {
                user.token = UUID.randomUUID().toString()
            }

            return@transaction user
        }
    }

    fun fetchUserByToken(token: String): User? {
        return transaction {
            User
                .find { Users.token eq token }
                .limit(1)
                .firstOrNull()
        }
    }

    fun fetchUser(id: Int): User? {
        return transaction {
            User
                .find { Users.id eq id }
                .limit(1)
                .firstOrNull()
        }
    }

    fun createDevice(address: String): Device {
        return transaction {
            Device.new {
                ipAddress = address
            }
        }
    }

    fun fetchDevice(id: Int): Device? {
        return transaction {
            Device
                .find { Devices.id eq id }
                .limit(1)
                .firstOrNull()
        }
    }
}