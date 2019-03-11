package no.bakkenbaeck.pppshared.api

import kotlinx.serialization.json.*

import no.bakkenbaeck.pppshared.model.*

object Api {

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

    /**
     * Locks a device.
     *
     * @param request The request to use to lock the device.
     * @param token The current user's token.
     * @return The updated lock state of the device
     */
    suspend fun lockDevice(
        request: DeviceRequest,
        token: String
    ): LockState {
        val lockStateJSON = client.execute(
            method = RequestMethod.Post(Json.stringify(DeviceRequest.serializer(), request)),
            path = "device/${request.deviceId}/lock",
            headers = listOf(
                    Header.AcceptJSON,
                    Header.TokenAuth(token)

            )
        )

        return Json.parse(LockState.serializer(), lockStateJSON)
    }

    /**
     * Unlocks a device.
     *
     * @param request The request to use to unlock the device.
     * @param token The current user's token.
     * @return The updated lock state of the device
     */
    suspend fun unlockDevice(
        request: DeviceRequest,
        token: String
    ): LockState {
        val lockStateJSON = client.execute(
            method = RequestMethod.Post(Json.stringify(DeviceRequest.serializer(), request)),
            path = "device/${request.deviceId}/unlock",
            headers = listOf(
                Header.AcceptJSON,
                Header.TokenAuth(token)
            )
        )

        return Json.parse(LockState.serializer(), lockStateJSON)
    }
}