package no.bakkenbaeck.porchpirateprotector.model

import no.bakkenbaeck.pppshared.model.LockState
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Devices: IntIdTable() {
    val ipAddress = varchar("ip", 50).nullable() // Column<String?>

    // TODO: Use actual checking of this rather than stuffing it in the database.
    val isLocked = bool("isLocked")
}

class Device(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Device>(Devices)

    var ipAddress by Devices.ipAddress
    var isLocked by Devices.isLocked
    val pairingKeys by PairingKey referrersOn PairingKeys.device

    fun toLockState(): LockState {
        return LockState(id.value, isLocked)
    }
}
