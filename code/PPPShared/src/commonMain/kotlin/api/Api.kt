package no.bakkenbaeck.pppshared.api

import kotlinx.serialization.json.*

import no.bakkenbaeck.pppshared.model.*

class Api {

    /// The client to use to execute these requests. Variable for testing.
    /// 10.0.2.2 is the emulator address for localhost
    var client = NetworkClient(rootURLString = "http://10.0.2.2:8080/api")

    /**
     * Creates a new user.
     *
     * @param credentials: The credentials to use to create the account
     * @return The user token on a successful account creation.
     */
    suspend fun createAccount(credentials: UserCredentials): UserToken {
        val tokenJSON = client.execute(
            method = RequestMethod.Post(Json.stringify(UserCredentials.serializer(), credentials)),
            path = "createAccount",
            headers = listOf(
                Header.AcceptJSON
            )
        )

        return Json.parse(UserToken.serializer(), tokenJSON)
    }

    /**
     * Logs an existing user in.
     *
     * @param credentials The credentials to use to log the user in
     * @return The user token on a successful login
     */
    suspend fun login(credentials: UserCredentials): UserToken {
        val tokenJSON = client.execute(
            method = RequestMethod.Post(Json.stringify(UserCredentials.serializer(), credentials)),
            path = "login",
            headers = listOf(
                Header.AcceptJSON
            )
        )

        println("PPP: $tokenJSON")

        return Json.parse(UserToken.serializer(), tokenJSON)
    }

    suspend fun addDevice(
        ipAddress: String,
        token: String
    ): DeviceRequest {
        val request = DeviceCreateRequest(ipAddress)
        val addDeviceJSON = client.execute(
            method = RequestMethod.Post(request.toJSONString()),
            path = "device/add",
            headers = listOf(
                Header.AcceptJSON,
                Header.TokenAuth(token)
            )
        )

        println("PPP: $addDeviceJSON")

        return Json.parse(DeviceRequest.serializer(), addDeviceJSON)
    }

    suspend fun getCurrentLockState(
        deviceId: Int,
        pairingKey: String,
        token: String
    ): LockState {
        val request = DeviceRequest(deviceId, pairingKey, null)
        val lockStateJSON = client.execute(
            method = RequestMethod.Post(request.toJSONString()),
            path = "device/status",
            headers = listOf(
                Header.AcceptJSON,
                Header.TokenAuth(token)
            )
        )

        println("PPP: $lockStateJSON")

        return Json.parse(LockState.serializer(), lockStateJSON)
    }

    /**
     * Locks or unlocks a device.
     *
     * @param request The request to use to lock or unlock the device.
     * @param token The current user's token.
     * @return The updated lock state of the device
     */
    suspend fun updateDeviceLockState(
        request: DeviceRequest,
        token: String
    ): LockState {
        val lockStateJSON = client.execute(
            method = RequestMethod.Post(Json.stringify(DeviceRequest.serializer(), request)),
            path = "device/lock",
            headers = listOf(
                Header.AcceptJSON,
                Header.TokenAuth(token)
            )
        )

        return Json.parse(LockState.serializer(), lockStateJSON)
    }
}