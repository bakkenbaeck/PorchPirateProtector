package no.bakkenbaeck.pppshared

import no.bakkenbaeck.pppshared.api.Header
import no.bakkenbaeck.pppshared.api.NetworkClient
import no.bakkenbaeck.pppshared.api.RequestMethod
import no.bakkenbaeck.pppshared.model.*
import kotlinx.serialization.json.*
import kotlin.text.*

class MockNetworkClient: NetworkClient("") {

    companion object {
        const val validUsername = "valid@seemslegit.biz"
        const val wrongPasswordUsername = "wrongpass@nope.org"
        const val takenUsername = "taken@nope.org"
        const val goodDeviceId = "1"
        const val badDeviceId = "2"
        const val mockToken = "I AM A MOCK TOKEN"
    }

    override suspend fun execute(method: RequestMethod, path: String, headers: List<Header>): String {
        return when (path) {
            "login" -> mockLogin(method)
            "createAccount" -> mockAccountCreate(method)
            "device/$goodDeviceId/lock" -> mockLock(path, method)
            "device/$badDeviceId/lock" -> mockLock(path, method)
            "device/$goodDeviceId/unlock" -> mockUnlock(path, method)
            "device/$badDeviceId/unlock" -> mockUnlock(path, method)
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

    private fun mockLock(path: String, method: RequestMethod): String {
        when (method) {
            is RequestMethod.Post -> {
                val request = Json.parse(DeviceRequest.serializer(), method.body)
                if (!path.contains(request.deviceId)) {
                    throw RuntimeException("Wrong device ID for path!")
                }
                when (path) {
                    "device/$goodDeviceId/lock" -> return mockLockState(goodDeviceId, true)
                    "device/$badDeviceId/lock" -> throw RuntimeException("Bad device ID! No donut!")
                    else -> throw RuntimeException("Unhandled device ID!")
                }

            }
            else -> throw RuntimeException("Invalid request method for locking device")
        }
    }

    private fun mockUnlock(path: String, method: RequestMethod): String {
        when (method) {
            is RequestMethod.Post -> {
                val request = Json.parse(DeviceRequest.serializer(), method.body)
                if (!path.contains(request.deviceId)) {
                    throw RuntimeException("Wrong device ID for path!")
                }
                when (path) {
                    "device/$goodDeviceId/unlock" -> return mockLockState(goodDeviceId, false)
                    "device/$badDeviceId/unlock" -> throw RuntimeException("Bad device ID! No donut!")
                    else -> throw RuntimeException("Unhandled device ID!")
                }

            }
            else -> throw RuntimeException("Invalid request method for unlocking device")
        }
    }

    private fun mockLockState(deviceId: String, isLocked: Boolean): String {
        val lockState = LockState(deviceId, isLocked)
        return Json.stringify(LockState.serializer(), lockState)
    }
}