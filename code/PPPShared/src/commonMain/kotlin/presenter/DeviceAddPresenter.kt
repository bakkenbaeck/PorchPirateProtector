package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.view.DeviceAddView

class DeviceAddPresenter(
    val view: DeviceAddView,
    storage: SecureStorage
): BaseCoroutinePresenter(secureStorage = storage) {

    fun updateAvailableIPAddresses() {
        view.updatedAvailableDeviceIPAddresses(DeviceManager.unpairedDeviceIpAddresses)
    }

    suspend fun addDeviceAsync(deviceIpAddress: String): PairedDevice? {
        view.startLoadingIndicator()
        view.pairingErrorUpdated(null)
        var pairedDevice: PairedDevice? = null
        try {
            pairedDevice = DeviceManager.pair(api, deviceIpAddress, throwingToken())
        } catch (exception: Exception) {
            view.pairingErrorUpdated(exception.message)
        }

        view.stopLoadingIndicator()
        view.updatedAvailableDeviceIPAddresses(DeviceManager.unpairedDeviceIpAddresses)
        pairedDevice?.let { view.deviceAddedSuccessfully(it) }
        return pairedDevice
    }

    fun addDevice(deviceIpAddress: String) {
        launch {
            addDeviceAsync(deviceIpAddress)
        }
    }
}