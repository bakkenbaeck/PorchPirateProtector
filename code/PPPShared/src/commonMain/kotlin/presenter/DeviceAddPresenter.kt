package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.InsecureStorage
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager

class DeviceAddPresenter: BaseCoroutinePresenter() {

    data class DeviceAddViewState(
        val availableIPAddresses: List<String>,
        val indicatorAnimating: Boolean = false,
        val deviceAdded: Boolean = false,
        val errorMessage: String? = null
    )

    private fun currentAvailableIPAddresses(insecureStorage: InsecureStorage): List<String> {
        return insecureStorage.loadIPAddresses() ?: emptyList()
    }

    fun initialViewState(insecureStorage: InsecureStorage): DeviceAddViewState {
        return DeviceAddViewState(
            availableIPAddresses = currentAvailableIPAddresses(insecureStorage)
        )
    }

    suspend fun addDeviceAsync(deviceIpAddress: String,
                               initialViewStateHandler: (DeviceAddViewState) -> Unit,
                               insecureStorage: InsecureStorage,
                               secureStorage: SecureStorage): DeviceAddViewState {
        initialViewStateHandler(
            DeviceAddViewState(
                availableIPAddresses = currentAvailableIPAddresses(insecureStorage),
                indicatorAnimating = true
            )
        )

        return try {
            val token = throwingToken(secureStorage)
            DeviceManager.pair(api, deviceIpAddress, token)

            // On success, remove the IP address from the list of IP addresses
            insecureStorage.removeIPAddress(deviceIpAddress)
            DeviceAddViewState(
                availableIPAddresses = currentAvailableIPAddresses(insecureStorage),
                deviceAdded = true
            )
        } catch (exception: Exception) {
            DeviceAddViewState(
                availableIPAddresses = currentAvailableIPAddresses(insecureStorage),
                errorMessage = exception.message
            )
        }
    }

    fun addDevice(deviceIpAddress: String,
                  initialViewStateHandler: (DeviceAddViewState) -> Unit,
                  insecureStorage: InsecureStorage,
                  secureStorage: SecureStorage,
                  completion: (DeviceAddViewState) -> Unit) {
        launch {
            val viewState = addDeviceAsync(
                deviceIpAddress,
                initialViewStateHandler,
                insecureStorage,
                secureStorage
            )
            completion(viewState)
        }
    }
}