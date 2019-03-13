package no.bakkenbaeck.pppshared.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DeviceCreateRequest(
    val ipAddress: String
) {

    companion object {
        fun fromJSONString(string: String): DeviceCreateRequest? {
            return try {
                Json.parse(DeviceCreateRequest.serializer(), string)
            } catch (exception: Exception) {
                println("PARSING ERROR: $exception")
                null
            }
        }
    }

    fun toJSONString(): String {
        return Json.stringify(DeviceCreateRequest.serializer(), this)
    }
}

@Serializable
data class DeviceRequest(
    val deviceId: Int,
    val pairingKey: String,
    val lockState: LockState?
) {
    companion object {
        fun fromJSONString(string: String): DeviceRequest? {
            return try {
                Json.parse(DeviceRequest.serializer(), string)
            } catch (exception: Exception) {
                println("PARSING ERROR: $exception")
                null
            }
        }
    }

    fun toJSONString(): String {
        return Json.stringify(DeviceRequest.serializer(), this)
    }
}