package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.view.DeviceListView

class DeviceListPresenter(
    val view: DeviceListView
): BaseCoroutinePresenter() {

    init {
        updateDeviceList()
    }

    fun updateDeviceList() {
        view.deviceListUpdated(DeviceManager.pairedDevices.toList())
        view.setAddButtonEnabled(!DeviceManager.unpairedDeviceIpAddresses.isEmpty())
    }

    suspend fun fetchDeviceDetailsAsync(device: PairedDevice) {
        view.startLoadingIndicator()
        try {
            DeviceManager.updateStatus(api, device, throwingToken())
            updateDeviceList()
        } catch (exception: Exception) {
            view.apiErrorUpdated(exception.message)
        }

        view.stopLoadingIndicator()
    }

    fun fetchDeviceDetails(device: PairedDevice) {
        launch {
            fetchDeviceDetailsAsync(device)
        }
    }

    fun selectedDevice(device: PairedDevice) {
        view.showDetailForDevice(device)
    }
}