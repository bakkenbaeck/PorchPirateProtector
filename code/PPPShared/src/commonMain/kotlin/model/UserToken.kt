package no.bakkenbaeck.pppshared.model

data class UserToken(val token: String) {

    companion object {
        fun fromJSONString(json: String): UserToken {
            // TODO: Actually get token from json
            return UserToken("TOKEN")
        }
    }

}