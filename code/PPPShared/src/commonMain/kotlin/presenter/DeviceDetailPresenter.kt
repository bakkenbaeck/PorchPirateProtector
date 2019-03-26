package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.LockState
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.view.DeviceDetailView

class DeviceDetailPresenter(
    val view: DeviceDetailView,
    val device: PairedDevice,
    storage: SecureStorage
): BaseCoroutinePresenter(secureStorage = storage) {

    init {
        view.setTitle("Device #${device.deviceId}")
    }


    private fun updateLocalLockState(fromDevices: List<PairedDevice>): LockState? {
        val updatedDevice = fromDevices.first { it.deviceId == device.deviceId }
        device.lockState = updatedDevice.lockState
        var lockState: LockState? = null
        updatedDevice.lockState?.let {
            lockState = it
            view.setLockButtonEnabled(!it.isLocked)
            view.setUnlockButtonEnabled(it.isLocked)
        }

        return lockState
    }

    suspend fun getStatusAsync(): LockState? {
        view.startLoadingIndicator()
        view.setLockButtonEnabled(false)
        view.setUnlockButtonEnabled(false)
        view.setApiError(null)
        var lockState: LockState? = null
        try {
            val pairedDevices = DeviceManager.updateStatus(api, device, throwingToken())
            lockState = updateLocalLockState(pairedDevices)
        } catch (exception: Exception) {
            view.setApiError(exception.message)
        }

        view.stopLoadingIndicator()
        return lockState
    }

    suspend fun lockAsync(): LockState? {
        view.startLoadingIndicator()
        view.setLockButtonEnabled(false)
        view.setUnlockButtonEnabled(false)
        view.setApiError(null)
        var lockState: LockState? = null
        try {
            val updatedDevices = DeviceManager.updateLockState(api, device, throwingToken(), true)
            lockState = updateLocalLockState(updatedDevices)
        } catch (exception: Exception) {
            view.setApiError(exception.message)
            view.setLockButtonEnabled(true)
        }

        view.stopLoadingIndicator()
        return lockState
    }

    suspend fun unlockAsync(): LockState? {
        view.startLoadingIndicator()
        view.setLockButtonEnabled(false)
        view.setUnlockButtonEnabled(false)
        view.setApiError(null)
        var lockState: LockState? = null
        try {
            val updatedDevices = DeviceManager.updateLockState(api, device, throwingToken(), false)
            lockState = updateLocalLockState(updatedDevices)
        } catch (exception: Exception) {
            view.setApiError(exception.message)
            view.setUnlockButtonEnabled(true)
        }

        view.stopLoadingIndicator()
        return lockState
    }

    fun getStatus() {
        launch {
            getStatusAsync()
        }
    }

    fun lock() {
        launch {
            lockAsync()
        }
    }

    fun unlock() {
        launch {
            unlockAsync()
        }
    }
}