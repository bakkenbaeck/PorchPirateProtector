package no.bakkenbaeck.pppshared.presenter

import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.interfaces.SecureStorage
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

    suspend fun getStatusAsync(): LockState? {
        view.startLoadingIndicator()
        view.setLockButtonEnabled(false)
        view.setUnlockButtonEnabled(false)
        view.setApiError(null)
        var lockState: LockState? = null
        try {
            lockState = api.getCurrentLockState(device.deviceId, device.pairingKey, throwingToken())
            device.lockState = lockState
            view.setLockButtonEnabled(!lockState.isLocked)
            view.setUnlockButtonEnabled(lockState.isLocked)
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
            lockState = api.updateDeviceLockState(device.createRequest(true), throwingToken())
            device.lockState = lockState
            view.setUnlockButtonEnabled(true)
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
            lockState = api.updateDeviceLockState(device.createRequest(false), throwingToken())
            device.lockState = lockState
            view.setLockButtonEnabled(true)
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