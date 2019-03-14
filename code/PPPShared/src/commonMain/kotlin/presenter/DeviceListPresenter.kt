package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.view.DeviceListView

class DeviceListPresenter(
    val view: DeviceListView,
    storage: SecureStorage
): BaseCoroutinePresenter(secureStorage = storage) {

    init {
        updateDeviceList()
    }

    fun updateDeviceList() {
        view.deviceListUpdated(DeviceManager.pairedDevices.toList())
        view.setAddButtonEnabled(!DeviceManager.unpairedDeviceIpAddresses.isEmpty())
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