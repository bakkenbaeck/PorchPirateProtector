package no.bakkenbaeck.pppshared.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceRequest(
    val deviceId: String,
    val pairingKey: String
)