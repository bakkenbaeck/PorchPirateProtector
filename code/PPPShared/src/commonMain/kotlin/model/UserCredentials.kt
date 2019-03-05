package no.bakkenbaeck.pppshared.model

data class UserCredentials(
    val username: String,
    val password: String
) {

    fun toJSONString(): String {
        return "{\"username\":$username,\"password\":$password}"
    }
}