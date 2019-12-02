package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice

class DeviceDetailPresenter(
    val device: PairedDevice
): BaseCoroutinePresenter() {

    data class DeviceDetailViewModel(
        val lockButtonEnabled: Boolean,
        val unlockButtonEnabled: Boolean,
        val errorMessage: String? = null,
        val indicatorAnimating: Boolean = false
    )

    val title: String
        get() = "Device #${device.deviceId}"

    private fun generateViewModel(fromDevices: List<PairedDevice>): DeviceDetailViewModel {
        val updatedDevice = fromDevices.first { it.deviceId == device.deviceId }
        device.lockState = updatedDevice.lockState
        return device.lockState?.isLocked?.let { isLocked ->
            DeviceDetailViewModel(
                lockButtonEnabled = !isLocked,
                unlockButtonEnabled = isLocked
            )
        } ?: DeviceDetailViewModel(
            lockButtonEnabled = false,
            unlockButtonEnabled = false,
            errorMessage = "Lock state unknown"
        )
    }

    suspend fun getStatusAsync(initialViewModelHandler: (DeviceDetailViewModel) -> Unit,
                               secureStorage: SecureStorage): DeviceDetailViewModel {
        initialViewModelHandler(DeviceDetailViewModel(
                lockButtonEnabled = false,
                unlockButtonEnabled = false,
                indicatorAnimating = true
            )
        )

        return try {
            val token = throwingToken(secureStorage)
            val pairedDevices = DeviceManager.updateStatus(api, device, token)
            generateViewModel(pairedDevices)
        } catch (exception: Exception) {
            DeviceDetailViewModel(
                lockButtonEnabled = false,
                unlockButtonEnabled = false,
                errorMessage = exception.message
            )
        }
    }

    suspend fun lockAsync(initialViewModelHandler: (DeviceDetailViewModel) -> Unit,
                          secureStorage: SecureStorage): DeviceDetailViewModel {
        initialViewModelHandler(DeviceDetailViewModel(
                lockButtonEnabled = false,
                unlockButtonEnabled = false,
                indicatorAnimating = true
            )
        )

        return try {
            val token = throwingToken(secureStorage)
            val updatedDevices = DeviceManager.updateLockState(api, device, token, true)
            generateViewModel(updatedDevices)
        } catch (exception: Exception) {
            DeviceDetailViewModel(
                lockButtonEnabled = true,
                unlockButtonEnabled = false,
                errorMessage = exception.message
            )
        }
    }

    suspend fun unlockAsync(initialViewModelHandler: (DeviceDetailViewModel) -> Unit,
                            secureStorage: SecureStorage): DeviceDetailViewModel {
        initialViewModelHandler(DeviceDetailViewModel(
                lockButtonEnabled = false,
                unlockButtonEnabled = false,
                indicatorAnimating = true
            )
        )
        return try {
            val token = throwingToken(secureStorage)
            val updatedDevices = DeviceManager.updateLockState(api, device, token, false)
            generateViewModel(updatedDevices)
        } catch (exception: Exception) {
            DeviceDetailViewModel(
                lockButtonEnabled = false,
                unlockButtonEnabled = true,
                errorMessage = exception.message
            )
        }
    }

    fun getStatus(initialViewModelHandler: (DeviceDetailViewModel) -> Unit,
                  secureStorage: SecureStorage,
                  completion: (DeviceDetailViewModel) -> Unit) {
        launch {
            val viewModel = getStatusAsync(initialViewModelHandler, secureStorage)
            completion(viewModel)
        }
    }

    fun lock(initialViewModelHandler: (DeviceDetailViewModel) -> Unit,
             secureStorage: SecureStorage,
             completion: (DeviceDetailViewModel) -> Unit) {
        launch {
            val viewModel = lockAsync(initialViewModelHandler, secureStorage)
            completion(viewModel)
        }
    }

    fun unlock(initialViewModelHandler: (DeviceDetailViewModel) -> Unit,
               secureStorage: SecureStorage,
               completion: (DeviceDetailViewModel) -> Unit) {
        launch {
            val viewModel = unlockAsync(initialViewModelHandler, secureStorage)
            completion(viewModel)
        }
    }
}