package no.bakkenbaeck.pppshared.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserCredentials(
    val username: String,
    val password: String
) {

    companion object {
        fun fromString(string: String): UserCredentials? {
            return Json.parse(UserCredentials.serializer(), string)
        }
    }
}