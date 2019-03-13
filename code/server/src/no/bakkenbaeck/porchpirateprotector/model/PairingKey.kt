package no.bakkenbaeck.porchpirateprotector.model

import no.bakkenbaeck.pppshared.model.DeviceRequest
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object PairingKeys: IntIdTable() {

    val user = reference("user", Users)
    val device = reference("device", Devices)
    val key = varchar("key", 50)
}

class PairingKey(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<PairingKey>(PairingKeys)

    var user by User referencedOn PairingKeys.user
    var device by Device referencedOn PairingKeys.device

    // TODO: Key should probably actually be stored on the device, not on the server.
    var key by PairingKeys.key

    // NOTE: This should only be called from within a database transaction
    //       since it accesses values in other tables.
    fun toDeviceRequest(): DeviceRequest {
        return DeviceRequest(
            device.id.value,
            key,
            device.toLockState()
        )
    }
}