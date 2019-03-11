package no.bakkenbaeck.porchpirateprotector.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Devices: IntIdTable() {
    val ipAddress = varchar("ip", 50).nullable() // Column<String?>
}

class Device(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<Device>(Devices)

    var ipAddress by Devices.ipAddress
}
