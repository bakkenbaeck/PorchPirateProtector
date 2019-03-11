package no.bakkenbaeck.pppshared.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DeviceRequest(
    val deviceId: String,
    val pairingKey: String
) {
    companion object {
        fun fromJSONString(string: String): DeviceRequest? {
            return Json.parse(DeviceRequest.serializer(), string)
        }
    }
}