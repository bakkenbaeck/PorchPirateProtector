package no.bakkenbaeck.porchpirateprotector

import no.bakkenbaeck.porchpirateprotector.model.*
import no.bakkenbaeck.pppshared.model.DeviceRequest
import no.bakkenbaeck.pppshared.model.LockState
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
        val serverIP = "172.17.0.3"

        /// The MySQL username for you database.
        val databaseUsername = "ppp-database"

        /// The password for your user in the database.
        /// For the love of god, use something more secure than this.
        val databasePassword = "password"

        /// The name of the actual database you want to put data into.
        /// Make sure this database already exists before trying to run the server.
        /// > mysql -u {username} -p
        /// > [enter your password when prompted]
        /// > create table {databaseName}
        val databaseName = "ppp"

        database = Database.connect("jdbc:mysql://$serverIP:3306/$databaseName",
            user = databaseUsername,
            password = databasePassword,
            driver = "com.mysql.jdbc.Driver"
        )
        transaction {
            SchemaUtils.create(Users, Devices, PairingKeys)
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

    fun validateDeviceRequest(deviceRequest: DeviceRequest, userId: Int): Device? {
        return transaction {
            val key = PairingKey
                .find {
                    PairingKeys.key eq deviceRequest.pairingKey
                }
                .limit(1)
                .firstOrNull() ?: return@transaction null

            return@transaction if (key.device.id.value == deviceRequest.deviceId
                    && key.user.id.value == userId) {
                key.device
            } else {
                null
            }
        }
    }

    fun addUser(userToAdd: User, toDevice: Device): DeviceRequest {
        return transaction {
            val key = PairingKey.new {
                device = toDevice
                user = userToAdd
                key = UUID.randomUUID().toString()
            }

            return@transaction key.toDeviceRequest()
        }
    }

    fun fetchOrCreateDevice(address: String): Device {
        return transaction {
            val fetched = Device
                .find { Devices.ipAddress eq address}
                .limit(1)
                .firstOrNull()

            return@transaction if (fetched != null) {
                fetched
            } else {
                Device.new {
                    ipAddress = address
                    isLocked = true
                }
            }
        }
    }

    fun updateDevice(device: Device, toState: LockState): LockState {
        return transaction {
            device.isLocked = toState.isLocked
            return@transaction LockState(device.id.value, device.isLocked)
        }
    }
}