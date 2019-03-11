package no.bakkenbaeck.pppshared.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserToken(
    val token: String
) {

    fun toJSONString(): String {
        return Json.stringify(UserToken.serializer(), this)
    }
}