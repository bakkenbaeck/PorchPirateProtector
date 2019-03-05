package no.bakkenbaeck.pppshared.api

import no.bakkenbaeck.pppshared.model.*

object Api {

    /// The client to use to execute these requests. Variable for testing.
    var client = NetworkClient(rootURLString = "http://localhost")

    /**
     * Creates a new user.
     *
     * @param credentials: The credentials to use to create the account
     * @return The user token on a successful account creation.
     */
    suspend fun createAccount(credentials: UserCredentials): UserToken {
        val tokenJSON = client.execute(
            method = RequestMethod.Post(credentials.toJSONString()),
            path = "createAccount",
            headers = listOf(
                Header.ContentTypeJSON,
                Header.AcceptJSON
            )
        )

        return UserToken.fromJSONString(tokenJSON)
    }

    /**
     * Logs an existing user in.
     *
     * @param credentials The credentials to use to log the user in
     * @return The user token on a successful login
     */
    suspend fun login(credentials: UserCredentials): UserToken {
        val tokenJSON = client.execute(
            method = RequestMethod.Post(credentials.toJSONString()),
            path = "login",
            headers = listOf(
                Header.ContentTypeJSON,
                Header.AcceptJSON
            )
        )

        return UserToken.fromJSONString(tokenJSON)
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
            method = RequestMethod.Post(request.toJSONString()),
            path = "device/${request.deviceId}/lock",
            headers = listOf(
                    Header.ContentTypeJSON,
                    Header.AcceptJSON,
                    Header.TokenAuth(token)

            )
        )

        return LockState.fromJSONString(lockStateJSON)
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
            method = RequestMethod.Post(request.toJSONString()),
            path = "device/${request.deviceId}/unlock",
            headers = listOf(
                Header.ContentTypeJSON,
                Header.AcceptJSON,
                Header.TokenAuth(token)

            )
        )

        return LockState.fromJSONString(lockStateJSON)
    }
}