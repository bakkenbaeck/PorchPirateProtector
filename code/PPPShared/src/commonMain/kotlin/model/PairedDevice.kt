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
        @kotlinx.serialization.UnstableDefault
        fun fromJSONString(string: String): PairedDevice? {
            return Json.parse(PairedDevice.serializer(), string)
        }
    }

    fun createRequest(wantedLocked: Boolean): DeviceRequest {
        return DeviceRequest(this.deviceId, this.pairingKey, LockState(this.deviceId, wantedLocked))
    }

    @kotlinx.serialization.UnstableDefault
    fun toJSONString(): String {
        return Json.stringify(PairedDevice.serializer(), this)
    }

    fun lockStateEmoji(): String {
        return when (lockState?.isLocked) {
            true -> "🔐"
            false -> "🔓"
            null -> "❓"
        }
    }

    fun displayName(): String {
        return "Device #$deviceId"
    }
}