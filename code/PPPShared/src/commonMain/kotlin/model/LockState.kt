package no.bakkenbaeck.pppshared.model

import kotlinx.serialization.Serializable

@Serializable
data class LockState(
    val deviceId: String,
    val isLocked: Boolean
)