package no.bakkenbaeck.pppshared.model

import kotlinx.serialization.Serializable

@Serializable
data class UserToken(
    val token: String
)