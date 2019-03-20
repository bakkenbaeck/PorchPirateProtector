package no.bakkenbaeck.pppshared.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PairedDevice(
    val deviceId: Int,
    val ipAddress: String,
    val pairingKey: String,
    var lockState: LockState?
) {

    companion object {
        fun fromJSONString(string: String): PairedDevice? {
            return Json.parse(PairedDevice.serializer(), string)
        }
    }

    fun createRequest(wantedLocked: Boolean): DeviceRequest {
        return DeviceRequest(this.deviceId, this.pairingKey, LockState(this.deviceId, wantedLocked))
    }

    fun toJSONString(): String {
        return Json.stringify(PairedDevice.serializer(), this)
    }

    val lockStateEmoji: String
        get() = when (lockState?.isLocked) {
        true -> "🔐"
        false -> "🔓"
        null -> "❓"
    }

    val deviceName: String
        get() = "Device #$deviceId"
}