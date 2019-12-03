package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.InsecureStorage
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice

class DeviceListPresenter: BaseCoroutinePresenter() {

    data class DeviceListViewModel(
        val pairedDeviceList: List<PairedDevice>,
        val addButtonEnabled: Boolean = true,
        val indicatorAnimating: Boolean = false,
        val apiError: String? = null
    )

    fun updateViewModel(insecureStorage: InsecureStorage,
                        isLoading: Boolean = false,
                        apiError: String? = null): DeviceListViewModel {
        val existingDevices = DeviceManager.loadPairedDevicesFromDatabase()
        val unpairedDevices = insecureStorage.loadIPAddresses() ?: emptyList()

        val enableAddButton = unpairedDevices.isNotEmpty() && !isLoading

        return DeviceListViewModel(
            pairedDeviceList = existingDevices,
            addButtonEnabled = enableAddButton,
            indicatorAnimating = isLoading,
            apiError = apiError
        )
    }

    suspend fun fetchDeviceDetailsAsync(device: PairedDevice,
                                        initialViewModelHandler: (DeviceListViewModel) -> Unit,
                                        secureStorage: SecureStorage,
                                        insecureStorage: InsecureStorage): DeviceListViewModel {
        initialViewModelHandler(
            updateViewModel(
                insecureStorage = insecureStorage,
                isLoading = true
            )
        )

        return try {
            val token = throwingToken(secureStorage)
            DeviceManager.updateStatus(api, device, token)
            updateViewModel(
                insecureStorage = insecureStorage
            )
        } catch (exception: Exception) {
            updateViewModel(
                insecureStorage = insecureStorage,
                apiError = exception.message
            )
        }
    }

    fun fetchDeviceDetails(device: PairedDevice,
                           initialViewModelHandler: (DeviceListViewModel) -> Unit,
                           secureStorage: SecureStorage,
                           insecureStorage: InsecureStorage,
                           completion: (DeviceListViewModel) -> Unit) {
        launch {
            val viewModel = fetchDeviceDetailsAsync(
                device,
                initialViewModelHandler,
                secureStorage,
                insecureStorage
            )

            completion(viewModel)
        }
    }
}