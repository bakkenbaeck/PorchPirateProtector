package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.InsecureStorage
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager

class DeviceAddPresenter: BaseCoroutinePresenter() {

    data class DeviceAddViewModel(
        val availableIPAddresses: List<String>,
        val indicatorAnimating: Boolean = false,
        val deviceAdded: Boolean = false,
        val errorMessage: String? = null
    )

    private fun currentAvailableIPAddresses(insecureStorage: InsecureStorage): List<String> {
        return insecureStorage.loadIPAddresses() ?: emptyList()
    }

    fun initialViewModel(insecureStorage: InsecureStorage): DeviceAddViewModel {
        return DeviceAddViewModel(
            availableIPAddresses = currentAvailableIPAddresses(insecureStorage)
        )
    }

    suspend fun addDeviceAsync(deviceIpAddress: String,
                               initialViewModelHandler: (DeviceAddViewModel) -> Unit,
                               insecureStorage: InsecureStorage,
                               secureStorage: SecureStorage): DeviceAddViewModel {
        initialViewModelHandler(
            DeviceAddViewModel(
                availableIPAddresses = currentAvailableIPAddresses(insecureStorage),
                indicatorAnimating = true
            )
        )

        return try {
            val token = throwingToken(secureStorage)
            DeviceManager.pair(api, deviceIpAddress, token)

            // On success, remove the IP address from the list of IP addresses
            insecureStorage.removeIPAddress(deviceIpAddress)
            DeviceAddViewModel(
                availableIPAddresses = currentAvailableIPAddresses(insecureStorage),
                deviceAdded = true
            )
        } catch (exception: Exception) {
            DeviceAddViewModel(
                availableIPAddresses = currentAvailableIPAddresses(insecureStorage),
                errorMessage = exception.message
            )
        }
    }

    fun addDevice(deviceIpAddress: String,
                  initialViewModelHandler: (DeviceAddViewModel) -> Unit,
                  insecureStorage: InsecureStorage,
                  secureStorage: SecureStorage,
                  completion: (DeviceAddViewModel) -> Unit) {
        launch {
            val viewModel = addDeviceAsync(
                deviceIpAddress,
                initialViewModelHandler,
                insecureStorage,
                secureStorage
            )
            completion(viewModel)
        }
    }
}