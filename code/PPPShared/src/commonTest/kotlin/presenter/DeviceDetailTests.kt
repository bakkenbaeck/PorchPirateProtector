package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.platformRunBlocking
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.mock.*
import no.bakkenbaeck.pppshared.model.LockState
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.view.DeviceDetailView
import kotlin.test.*

class DeviceDetailTests {

    class TestDetailView: DeviceDetailView {
        var viewTitle: String? = null
        override fun setTitle(toString: String) {
            viewTitle = toString
        }

        var lockEnabled = false
        var lockWasDisabled = false
        override fun setLockButtonEnabled(enabled: Boolean) {
            lockEnabled = enabled
            if (!enabled) {
                lockWasDisabled = true
            }
        }

        var unlockEnabled = false
        var unlockWasDisabled = false
        override fun setUnlockButtonEnabled(enabled: Boolean) {
            unlockEnabled = enabled
            if (!enabled) {
                unlockWasDisabled = true
            }
        }

        var apiErrorString: String? = null
        override fun setApiError(toString: String?) {
            apiErrorString = toString
        }

        var loadingIndicatorGoing = false
        var loadingIndicatorStarted = false
        override fun startLoadingIndicator() {
            loadingIndicatorGoing = true
            loadingIndicatorStarted = true
        }

        var loadingIndicatorStopped = false
        override fun stopLoadingIndicator() {
            loadingIndicatorGoing = false
            loadingIndicatorStopped = true
        }
    }

    @Test
    fun fetchingCurrentStateWithValidKeySucceeds() = platformRunBlocking {
        val view = TestDetailView()
        val storage = MockSecureStorage()
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = MockNetworkClient.validPairingKey,
            lockState = null
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(view, device, storage)
        presenter.api.client = MockNetworkClient()

        assertNull(presenter.device.lockState)

        val lockState = presenter.getStatusAsync()

        assertTrue(view.lockWasDisabled)
        assertTrue(view.unlockWasDisabled)
        assertTrue(view.loadingIndicatorStarted)
        assertTrue(view.loadingIndicatorStopped)
        assertFalse(view.loadingIndicatorGoing)

        assertFalse(view.lockEnabled)
        assertTrue(view.unlockEnabled)

        // Is the presenter device updated?
        assertEquals(true, presenter.device.lockState?.isLocked)

        // Did we actually get the correct value back from the API?
        lockState?.let {
            assertTrue(it.isLocked)
            assertEquals(device.deviceId, it.deviceId)
        }?: fail("Did not get lock state with valid login info!")

        // Was the device updated in the device list as well?
        val firstDevice = DeviceManager.loadPairedDevicesFromDatabase().first()
        firstDevice.lockState?.let {
            assertTrue(it.isLocked)
            assertEquals(device.deviceId, it.deviceId)
        } ?: fail("Couldn't get device lock state!")
    }

    @Test
    fun fetchingCurrentStateWithInvalidKeyFails() = platformRunBlocking {
        val view = TestDetailView()
        val storage = MockSecureStorage()
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = "LOL NO",
            lockState = null
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(view, device, storage)
        presenter.api.client = MockNetworkClient()

        assertNull(presenter.device.lockState)

        val lockState = presenter.getStatusAsync()

        assertNull(lockState)
        assertEquals("Invalid pairing key!", view.apiErrorString)

        assertTrue(view.lockWasDisabled)
        assertTrue(view.unlockWasDisabled)
        assertTrue(view.loadingIndicatorStarted)
        assertTrue(view.loadingIndicatorStopped)
        assertFalse(view.loadingIndicatorGoing)

        assertFalse(view.lockEnabled)
        assertFalse(view.unlockEnabled)

        assertNull(presenter.device.lockState)
    }

    @Test
    fun lockingUnlockedDeviceWithValidKeySucceeds() = platformRunBlocking {
        val view = TestDetailView()
        val storage = MockSecureStorage()
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.unlockedDeviceId,
            ipAddress = "5.6.7",
            pairingKey = MockNetworkClient.validPairingKey,
            lockState = LockState(MockNetworkClient.unlockedDeviceId, false)
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(view, device, storage)
        presenter.api.client = MockNetworkClient()

        // before we start, is it unlocked?
        assertEquals(false, presenter.device.lockState?.isLocked)

        val lockState = presenter.lockAsync()

        assertTrue(view.lockWasDisabled)
        assertTrue(view.unlockWasDisabled)
        assertTrue(view.loadingIndicatorStarted)
        assertTrue(view.loadingIndicatorStopped)
        assertFalse(view.loadingIndicatorGoing)

        assertFalse(view.lockEnabled)
        assertTrue(view.unlockEnabled)

        // Is the presenter device updated?
        assertEquals(true, presenter.device.lockState?.isLocked)

        // Did we actually get the correct value back from the API?
        lockState?.let {
            assertTrue(it.isLocked)
            assertEquals(device.deviceId, it.deviceId)
        }?: fail("Did not get lock state with valid login info!")

        // Was the device updated in the device list as well?
        val firstDevice = DeviceManager.loadPairedDevicesFromDatabase().first()
        firstDevice.lockState?.let {
            assertTrue(it.isLocked)
            assertEquals(device.deviceId, it.deviceId)
        } ?: fail("Couldn't get device lock state!")
    }

    @Test
    fun lockingUnlockedDeviceWithInvalidKeyFails() = platformRunBlocking {
        val view = TestDetailView()
        val storage = MockSecureStorage()
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.unlockedDeviceId,
            ipAddress = "4.5.6",
            pairingKey = "LOL NO",
            lockState = LockState(MockNetworkClient.unlockedDeviceId, false)
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(view, device, storage)
        presenter.api.client = MockNetworkClient()

        // before we start, is it unlocked?
        assertEquals(false, presenter.device.lockState?.isLocked)

        val lockState = presenter.lockAsync()

        assertNull(lockState)
        assertEquals("Invalid pairing key!", view.apiErrorString)

        assertTrue(view.lockWasDisabled)
        assertTrue(view.unlockWasDisabled)
        assertTrue(view.loadingIndicatorStarted)
        assertTrue(view.loadingIndicatorStopped)
        assertFalse(view.loadingIndicatorGoing)

        assertTrue(view.lockEnabled)
        assertFalse(view.unlockEnabled)

        // We should still think the device is unlocked.
        assertEquals(false, presenter.device.lockState?.isLocked)
        assertEquals(false, DeviceManager.loadPairedDevicesFromDatabase().first().lockState?.isLocked)
    }

    @Test
    fun unlockingLockedDeviceWithValidKeySucceeds() = platformRunBlocking {
        val view = TestDetailView()
        val storage = MockSecureStorage()
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = MockNetworkClient.validPairingKey,
            lockState = LockState(MockNetworkClient.unlockedDeviceId, true)
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(view, device, storage)
        presenter.api.client = MockNetworkClient()

        // before we start, is it locked?
        assertEquals(true, presenter.device.lockState?.isLocked)

        val lockState = presenter.unlockAsync()

        assertTrue(view.lockWasDisabled)
        assertTrue(view.unlockWasDisabled)
        assertTrue(view.loadingIndicatorStarted)
        assertTrue(view.loadingIndicatorStopped)
        assertFalse(view.loadingIndicatorGoing)

        assertTrue(view.lockEnabled)
        assertFalse(view.unlockEnabled)

        // Is the presenter device updated?
        assertEquals(false, presenter.device.lockState?.isLocked)

        // Did we actually get the correct value back from the API?
        lockState?.let {
            assertFalse(it.isLocked)
            assertEquals(device.deviceId, it.deviceId)
        }?: fail("Did not get lock state with valid login info!")

        // Was the device updated in the device list as well?
        val firstDevice = DeviceManager.loadPairedDevicesFromDatabase().first()
        firstDevice.lockState?.let {
            assertFalse(it.isLocked)
            assertEquals(device.deviceId, it.deviceId)
        } ?: fail("Couldn't get device lock state!")
    }

    @Test
    fun unlockingLockedDeviceWithInvalidKeyFails() = platformRunBlocking {
        val view = TestDetailView()
        val storage = MockSecureStorage()
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = "LOL NO",
            lockState = LockState(MockNetworkClient.lockedDeviceId, true)
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(view, device, storage)
        presenter.api.client = MockNetworkClient()

        // before we start, is it locked?
        assertEquals(true, presenter.device.lockState?.isLocked)

        val lockState = presenter.unlockAsync()

        assertNull(lockState)
        assertEquals("Invalid pairing key!", view.apiErrorString)

        assertTrue(view.lockWasDisabled)
        assertTrue(view.unlockWasDisabled)
        assertTrue(view.loadingIndicatorStarted)
        assertTrue(view.loadingIndicatorStopped)
        assertFalse(view.loadingIndicatorGoing)

        assertFalse(view.lockEnabled)
        assertTrue(view.unlockEnabled)

        // We should still think the device is locked.
        assertEquals(true, presenter.device.lockState?.isLocked)
        assertEquals(true, DeviceManager.loadPairedDevicesFromDatabase().first().lockState?.isLocked)
    }
}
