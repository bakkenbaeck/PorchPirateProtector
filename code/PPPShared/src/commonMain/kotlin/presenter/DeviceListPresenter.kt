package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.InsecureStorage
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice

class DeviceListPresenter: BaseCoroutinePresenter() {

    data class DeviceListViewState(
        val pairedDeviceList: List<PairedDevice>,
        val addButtonEnabled: Boolean = true,
        val indicatorAnimating: Boolean = false,
        val apiError: String? = null
    )

    fun updateViewState(insecureStorage: InsecureStorage,
                        isLoading: Boolean = false,
                        apiError: String? = null): DeviceListViewState {
        val existingDevices = DeviceManager.loadPairedDevicesFromDatabase()
        val unpairedDevices = insecureStorage.loadIPAddresses() ?: emptyList()

        val enableAddButton = unpairedDevices.isNotEmpty() && !isLoading

        return DeviceListViewState(
            pairedDeviceList = existingDevices,
            addButtonEnabled = enableAddButton,
            indicatorAnimating = isLoading,
            apiError = apiError
        )
    }

    suspend fun fetchDeviceDetailsAsync(device: PairedDevice,
                                        initialViewStateHandler: (DeviceListViewState) -> Unit,
                                        secureStorage: SecureStorage,
                                        insecureStorage: InsecureStorage): DeviceListViewState {
        initialViewStateHandler(
            updateViewState(
                insecureStorage = insecureStorage,
                isLoading = true
            )
        )

        return try {
            val token = throwingToken(secureStorage)
            DeviceManager.updateStatus(api, device, token)
            updateViewState(
                insecureStorage = insecureStorage
            )
        } catch (exception: Exception) {
            updateViewState(
                insecureStorage = insecureStorage,
                apiError = exception.message
            )
        }
    }

    fun fetchDeviceDetails(device: PairedDevice,
                           initialViewStateHandler: (DeviceListViewState) -> Unit,
                           secureStorage: SecureStorage,
                           insecureStorage: InsecureStorage,
                           completion: (DeviceListViewState) -> Unit) {
        launch {
            val viewState = fetchDeviceDetailsAsync(
                device,
                initialViewStateHandler,
                secureStorage,
                insecureStorage
            )

            completion(viewState)
        }
    }
}