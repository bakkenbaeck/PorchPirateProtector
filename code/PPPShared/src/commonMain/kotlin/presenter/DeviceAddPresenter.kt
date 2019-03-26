package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.InsecureStorage
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.view.DeviceAddView

class DeviceAddPresenter(
    val view: DeviceAddView,
    storage: SecureStorage,
    private val insecureStorage: InsecureStorage
): BaseCoroutinePresenter(secureStorage = storage) {

    fun updateAvailableIPAddresses() {
        val addresses = insecureStorage.loadIPAddresses() ?: emptyList()
        view.updatedAvailableDeviceIPAddresses(addresses)
    }

    suspend fun addDeviceAsync(deviceIpAddress: String): List<PairedDevice>? {
        view.startLoadingIndicator()
        view.pairingErrorUpdated(null)
        var pairedDevices: List<PairedDevice>? = null
        try {
            pairedDevices = DeviceManager.pair(api, deviceIpAddress, throwingToken())
            insecureStorage.removeIPAddress(deviceIpAddress)
        } catch (exception: Exception) {
            view.pairingErrorUpdated(exception.message)
        }

        view.stopLoadingIndicator()

        return pairedDevices?.let { devices ->
            view.updatedAvailableDeviceIPAddresses(insecureStorage.loadIPAddresses() ?: emptyList())
            val addedDevice = devices.first { it.ipAddress == deviceIpAddress}
            view.deviceAddedSuccessfully(addedDevice)

            return@addDeviceAsync devices
        }
    }

    fun addDevice(deviceIpAddress: String) {
        launch {
            addDeviceAsync(deviceIpAddress)
        }
    }
}