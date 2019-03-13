package no.bakkenbaeck.pppshared.manager

import no.bakkenbaeck.pppshared.api.Api
import no.bakkenbaeck.pppshared.extension.indexOrNull
import no.bakkenbaeck.pppshared.model.PairedDevice

object DeviceManager {

    var pairedDevices: MutableList<PairedDevice> = mutableListOf()
    var unpairedDeviceIpAddresses: MutableList<String> = mutableListOf("10.0.0.3")


    private fun isAlreadyPaired(deviceIpAddress: String): Boolean {
        val paired = pairedDevices.firstOrNull { it.ipAddress == deviceIpAddress }
        return (paired != null)
    }

    private fun pairedDeviceWithId(deviceIpAddress: String): PairedDevice {
        return pairedDevices.first { it.ipAddress == deviceIpAddress }
    }

    suspend fun pair(
        api: Api,
        deviceIpAddress: String,
        token: String
    ): PairedDevice {
        if (isAlreadyPaired(deviceIpAddress)) {
            return pairedDeviceWithId(deviceIpAddress)
        }

        // TODO: Make this actually do some bluetooth pairing. For now, play nice with the server:
        val deviceRequest = api.addDevice(deviceIpAddress, token)
        val pairedDevice = PairedDevice(deviceRequest.deviceId,
            deviceIpAddress,
            deviceRequest.pairingKey,
            deviceRequest.lockState)

        unpairedDeviceIpAddresses.indexOrNull(deviceIpAddress)?.let {
            unpairedDeviceIpAddresses.removeAt(it)
        }

        pairedDevices.add(pairedDevice)
        return pairedDevice
    }

    suspend fun updateStatus(
        api: Api,
        device: PairedDevice,
        token: String
    ): List<PairedDevice> {
        val lockState = api.getCurrentLockState(device.deviceId, device.pairingKey, token)
        device.lockState = lockState
        val index = pairedDevices.indexOfFirst { it.deviceId == device.deviceId }
        if (index >= 0) {
            pairedDevices.removeAt(index)
            pairedDevices.add(index, device)
        }

        return pairedDevices.toList()
    }
}