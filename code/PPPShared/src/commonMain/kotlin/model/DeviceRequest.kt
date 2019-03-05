package no.bakkenbaeck.pppshared.model

data class DeviceRequest(
    val deviceId: String,
    val pairingKey: String
) {

    fun hashedPairingKey(): String {
        // TODO: Actually hash
        return pairingKey
    }

    fun toJSONString(): String {
        return "{\"deviceId\":$deviceId,\"requestKey\":${hashedPairingKey()}}"
    }
}