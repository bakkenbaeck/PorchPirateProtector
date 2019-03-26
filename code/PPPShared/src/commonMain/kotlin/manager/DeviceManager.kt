package no.bakkenbaeck.pppshared.manager

import no.bakkenbaeck.pppshared.api.Api
import no.bakkenbaeck.pppshared.db.MobileDb
import no.bakkenbaeck.pppshared.db.StoredDevice
import no.bakkenbaeck.pppshared.model.LockState
import no.bakkenbaeck.pppshared.model.PairedDevice

fun List<StoredDevice>.toPairedDevices(): List<PairedDevice> {
    return this.map { stored: StoredDevice ->
        var lockState: LockState? = null
        stored.lastKnownLockState?.let {
            lockState = LockState(stored.id, it)
        }
        PairedDevice(
            deviceId = stored.id,
            ipAddress = stored.address,
            pairingKey = stored.pairingKey,
            lockState  = lockState
        )
    }
}

object DeviceManager {

    fun loadPairedDevicesFromDatabase(): List<PairedDevice> {
        val deviceQueries = MobileDb.instance.storedDeviceQueries
        val storedDevices = deviceQueries.selectAll().executeAsList()
        return storedDevices.toPairedDevices()
    }

    internal fun storeDeviceToDatabase(device: PairedDevice): List<PairedDevice> {
        val deviceQueries = MobileDb.instance.storedDeviceQueries
        deviceQueries.insertOrUpdate(
            id = device.deviceId,
            address = device.ipAddress,
            pairingKey = device.pairingKey,
            lastKnownLockState = device.lockState?.isLocked
        )

        return loadPairedDevicesFromDatabase()
    }

    private fun updateStoredLockState(deviceID: Int, state: LockState?): List<PairedDevice> {
        val deviceQueries = MobileDb.instance.storedDeviceQueries
        deviceQueries.updateLastKnownLockState(state?.isLocked, deviceID)

        return loadPairedDevicesFromDatabase()
    }

    suspend fun pair(
        api: Api,
        deviceIpAddress: String,
        token: String
    ): List<PairedDevice> {
        // TODO: Make this actually do some bluetooth pairing. For now, play nice with the server:
        val deviceRequest = api.addDevice(deviceIpAddress, token)
        val pairedDevice = PairedDevice(deviceRequest.deviceId,
            deviceIpAddress,
            deviceRequest.pairingKey,
            deviceRequest.lockState)

        return storeDeviceToDatabase(pairedDevice)
    }

    suspend fun updateStatus(
        api: Api,
        device: PairedDevice,
        token: String
    ): List<PairedDevice> {
        val lockState = api.getCurrentLockState(device.deviceId, device.pairingKey, token)
        return updateStoredLockState(device.deviceId, lockState)
    }

    suspend fun updateLockState(
        api: Api,
        device: PairedDevice,
        token: String,
        wantedLocked: Boolean
    ): List<PairedDevice> {
        val lockState = api.updateDeviceLockState(device.createRequest(wantedLocked), token)
        return updateStoredLockState(device.deviceId, lockState)
    }
}