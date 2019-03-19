package no.bakkenbaeck.pppshared.mock

import no.bakkenbaeck.pppshared.api.Header
import no.bakkenbaeck.pppshared.api.NetworkClient
import no.bakkenbaeck.pppshared.api.RequestMethod
import no.bakkenbaeck.pppshared.model.*
import kotlinx.serialization.json.*

class MockNetworkClient: NetworkClient("") {

    companion object {
        const val validUsername = "valid@seemslegit.biz"
        const val wrongPasswordUsername = "wrongpass@nope.org"
        const val takenUsername = "taken@nope.org"
        const val lockedDeviceId = 1
        const val unlockedDeviceId = 2
        const val mockToken = "I AM A MOCK TOKEN"
        const val validPairingKey = "I AM A PAIRING KEY"
        const val lockedIP = "1.2.3"
    }

    override suspend fun execute(method: RequestMethod, path: String, headers: List<Header>): String {
        return when (path) {
            "login" -> mockLogin(method)
            "createAccount" -> mockAccountCreate(method)
            "device/lock" -> mockLock(method, headers)
            "device/status" -> mockStatus(method, headers)
            "device/add" -> mockAdd(method, headers)
            else -> throw RuntimeException("Unsupported API call!")
        }
    }

    private fun mockLogin(method: RequestMethod): String {
        when (method) {
            is RequestMethod.Post -> {
                val creds = Json.parse(UserCredentials.serializer(), method.body)
                when (creds.username) {
                    MockNetworkClient.validUsername -> return mockToken()
                    MockNetworkClient.wrongPasswordUsername -> throw RuntimeException("Wrong password")
                    else -> throw RuntimeException("Unhandled username in mocks")
                }
            }
            else -> throw RuntimeException("Invalid request for login")
        }
    }

    private fun mockAccountCreate(method: RequestMethod): String {
        when (method) {
            is RequestMethod.Post -> {
                val creds = Json.parse(UserCredentials.serializer(), method.body)
                when (creds.username) {
                    MockNetworkClient.validUsername -> return mockToken()
                    MockNetworkClient.takenUsername -> throw RuntimeException("Account already exists")
                    else -> throw RuntimeException("Nope ")
                }
            }
            else -> throw RuntimeException("Invalid request method for creating account")
        }
    }

    private fun mockToken(): String {
        val token = UserToken(MockNetworkClient.mockToken)
        return Json.stringify(UserToken.serializer(), token)
    }

    private fun mockLock(method: RequestMethod, headers: List<Header>): String {
        if (!headers.contains(Header.TokenAuth(mockToken))) {
            throw RuntimeException("Not authorized!")
        }

        when (method) {
            is RequestMethod.Post -> {
                val request = Json.parse(DeviceRequest.serializer(), method.body)
                if (request.pairingKey != validPairingKey) {
                    throw RuntimeException("Invalid pairing key!")
                }

                val lockState = request.lockState
                lockState?.let { validState ->
                    return mockLockState(request.deviceId, validState.isLocked)
                } ?: throw RuntimeException("Need a desired lock state!")
            }
            else -> throw RuntimeException("This only accepts posts!")
        }
    }

    private fun mockStatus(method: RequestMethod, headers: List<Header>): String {
        if (!headers.contains(Header.TokenAuth(mockToken))) {
            throw RuntimeException("Not authorized!")
        }

        when (method) {
            is RequestMethod.Post -> {
                val request = Json.parse(DeviceRequest.serializer(), method.body)
                if (request.pairingKey != validPairingKey) {
                    throw RuntimeException("Invalid pairing key!")
                }

                when (request.deviceId) {
                    lockedDeviceId -> return mockLockState(lockedDeviceId, true)
                    unlockedDeviceId -> throw RuntimeException("Bad device ID! No donut!")
                    else -> throw RuntimeException("Unhandled device ID!")
                }
            }
            else -> throw RuntimeException("Invalid request method for unlocking device")
        }
    }

    private fun mockAdd(method: RequestMethod, headers: List<Header>): String {
        if (!headers.contains(Header.TokenAuth(mockToken))) {
            throw RuntimeException("Not authorized!")
        }

        when (method) {
            is RequestMethod.Post -> {
                val request = Json.parse(DeviceCreateRequest.serializer(), method.body)
                val shouldBeLocked: Boolean
                val deviceId: Int
                when (request.ipAddress) {
                    lockedIP -> {
                        shouldBeLocked = true
                        deviceId = lockedDeviceId
                    }
                    else -> {
                        shouldBeLocked = false
                        deviceId = unlockedDeviceId
                    }
                }
                val deviceRequest = DeviceRequest(deviceId, validPairingKey, LockState(deviceId, shouldBeLocked))
                return Json.stringify(DeviceRequest.serializer(), deviceRequest)
            }
            else -> throw RuntimeException("Invalid request method for unlocking device")
        }
    }

    private fun mockLockState(deviceId: Int, isLocked: Boolean): String {
        val lockState = LockState(deviceId, isLocked)
        return Json.stringify(LockState.serializer(), lockState)
    }
}