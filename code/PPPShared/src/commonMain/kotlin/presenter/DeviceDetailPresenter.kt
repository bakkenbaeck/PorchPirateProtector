package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice

class DeviceDetailPresenter(
    val device: PairedDevice
): BaseCoroutinePresenter() {

    data class DeviceDetailViewState(
        val lockButtonEnabled: Boolean,
        val unlockButtonEnabled: Boolean,
        val errorMessage: String? = null,
        val indicatorAnimating: Boolean = false
    )

    val title: String
        get() = "Device #${device.deviceId}"

    private fun generateViewState(fromDevices: List<PairedDevice>): DeviceDetailViewState {
        val updatedDevice = fromDevices.first { it.deviceId == device.deviceId }
        device.lockState = updatedDevice.lockState
        return device.lockState?.isLocked?.let { isLocked ->
            DeviceDetailViewState(
                lockButtonEnabled = !isLocked,
                unlockButtonEnabled = isLocked
            )
        } ?: DeviceDetailViewState(
            lockButtonEnabled = false,
            unlockButtonEnabled = false,
            errorMessage = "Lock state unknown"
        )
    }

    suspend fun getStatusAsync(initialViewStateHandler: (DeviceDetailViewState) -> Unit,
                               secureStorage: SecureStorage): DeviceDetailViewState {
        initialViewStateHandler(DeviceDetailViewState(
                lockButtonEnabled = false,
                unlockButtonEnabled = false,
                indicatorAnimating = true
            )
        )

        return try {
            val token = throwingToken(secureStorage)
            val pairedDevices = DeviceManager.updateStatus(api, device, token)
            generateViewState(pairedDevices)
        } catch (exception: Exception) {
            DeviceDetailViewState(
                lockButtonEnabled = false,
                unlockButtonEnabled = false,
                errorMessage = exception.message
            )
        }
    }

    suspend fun lockAsync(initialViewStateHandler: (DeviceDetailViewState) -> Unit,
                          secureStorage: SecureStorage): DeviceDetailViewState {
        initialViewStateHandler(DeviceDetailViewState(
                lockButtonEnabled = false,
                unlockButtonEnabled = false,
                indicatorAnimating = true
            )
        )

        return try {
            val token = throwingToken(secureStorage)
            val updatedDevices = DeviceManager.updateLockState(api, device, token, true)
            generateViewState(updatedDevices)
        } catch (exception: Exception) {
            DeviceDetailViewState(
                lockButtonEnabled = true,
                unlockButtonEnabled = false,
                errorMessage = exception.message
            )
        }
    }

    suspend fun unlockAsync(initialViewStateHandler: (DeviceDetailViewState) -> Unit,
                            secureStorage: SecureStorage): DeviceDetailViewState {
        initialViewStateHandler(DeviceDetailViewState(
                lockButtonEnabled = false,
                unlockButtonEnabled = false,
                indicatorAnimating = true
            )
        )
        return try {
            val token = throwingToken(secureStorage)
            val updatedDevices = DeviceManager.updateLockState(api, device, token, false)
            generateViewState(updatedDevices)
        } catch (exception: Exception) {
            DeviceDetailViewState(
                lockButtonEnabled = false,
                unlockButtonEnabled = true,
                errorMessage = exception.message
            )
        }
    }

    fun getStatus(initialViewStateHandler: (DeviceDetailViewState) -> Unit,
                  secureStorage: SecureStorage,
                  completion: (DeviceDetailViewState) -> Unit) {
        launch {
            val viewState = getStatusAsync(initialViewStateHandler, secureStorage)
            completion(viewState)
        }
    }

    fun lock(initialViewStateHandler: (DeviceDetailViewState) -> Unit,
             secureStorage: SecureStorage,
             completion: (DeviceDetailViewState) -> Unit) {
        launch {
            val viewState = lockAsync(initialViewStateHandler, secureStorage)
            completion(viewState)
        }
    }

    fun unlock(initialViewStateHandler: (DeviceDetailViewState) -> Unit,
               secureStorage: SecureStorage,
               completion: (DeviceDetailViewState) -> Unit) {
        launch {
            val viewState = unlockAsync(initialViewStateHandler, secureStorage)
            completion(viewState)
        }
    }
}