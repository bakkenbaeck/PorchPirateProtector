package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.InsecureStorage
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.view.DeviceListView

class DeviceListPresenter(
    val view: DeviceListView,
    storage: SecureStorage,
    private val insecureStorage: InsecureStorage
): BaseCoroutinePresenter(secureStorage = storage) {

    init {
        updateDeviceList()
    }

    fun updateDeviceList() {
        val existingDevices = DeviceManager.loadPairedDevicesFromDatabase()
        view.deviceListUpdated(existingDevices)

        val unpairedDevices = insecureStorage.loadIPAddresses() ?: emptyList()
        view.setAddButtonEnabled(!unpairedDevices.isEmpty())
    }

    suspend fun fetchDeviceDetailsAsync(device: PairedDevice): List<PairedDevice>? {
        view.startLoadingIndicator()
        var devices: List<PairedDevice>? = null
        try {
            devices = DeviceManager.updateStatus(api, device, throwingToken())
            updateDeviceList()
        } catch (exception: Exception) {
            view.apiErrorUpdated(exception.message)
        }

        view.stopLoadingIndicator()
        return devices
    }

    fun fetchDeviceDetails(device: PairedDevice) {
        launch {
            fetchDeviceDetailsAsync(device)
        }
    }

    fun selectedDevice(device: PairedDevice) {
        view.showDetailForDevice(device)
    }

    fun selectedAddDevice() {
        view.showAddDevice()
    }
}