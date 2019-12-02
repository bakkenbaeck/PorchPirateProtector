package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.TestDb
import no.bakkenbaeck.pppshared.platformRunBlocking
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.mock.*
import no.bakkenbaeck.pppshared.model.LockState
import no.bakkenbaeck.pppshared.model.PairedDevice
import kotlin.test.*

class DeviceDetailTests {

    val storage = MockSecureStorage()

    @BeforeTest
    fun setup() {
        TestDb.setupIfNeeded()
    }

    @AfterTest
    fun tearDown(){
        TestDb.clearDatabase()
    }


    @Test
    fun fetchingCurrentStateWithValidKeySucceeds() = platformRunBlocking {
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = MockNetworkClient.validPairingKey,
            lockState = null
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(device)
        presenter.api.client = MockNetworkClient()

        assertNull(presenter.device.lockState)

        var initialHit = false
        val viewModel = presenter.getStatusAsync(
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertTrue(initialViewModel.indicatorAnimating)
                assertFalse(initialViewModel.lockButtonEnabled)
                assertFalse(initialViewModel.unlockButtonEnabled)
                assertNull(initialViewModel.errorMessage)
            },
            secureStorage = storage
        )

        assertFalse(viewModel.lockButtonEnabled)
        assertTrue(viewModel.unlockButtonEnabled)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.errorMessage)


        // Is the presenter device updated?
        assertEquals(true, presenter.device.lockState?.isLocked)

        // Was the device updated in the device list as well?
        val firstDevice = DeviceManager.loadPairedDevicesFromDatabase().first()
        firstDevice.lockState?.let {
            assertTrue(it.isLocked)
            assertEquals(device.deviceId, it.deviceId)
        } ?: fail("Couldn't get device lock state!")
    }

    @Test
    fun fetchingCurrentStateWithInvalidKeyFails() = platformRunBlocking {
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = "LOL NO",
            lockState = null
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(device)
        presenter.api.client = MockNetworkClient()

        assertNull(presenter.device.lockState)

        var initialHit = false
        val viewModel = presenter.getStatusAsync(
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertFalse(initialViewModel.lockButtonEnabled)
                assertFalse(initialViewModel.unlockButtonEnabled)
                assertTrue(initialViewModel.indicatorAnimating)
                assertNull(initialViewModel.errorMessage)
            },
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertFalse(viewModel.lockButtonEnabled)
        assertFalse(viewModel.unlockButtonEnabled)
        assertFalse(viewModel.indicatorAnimating)
        assertEquals("Invalid pairing key!", viewModel.errorMessage)

        assertNull(presenter.device.lockState)
    }

    @Test
    fun lockingUnlockedDeviceWithValidKeySucceeds() = platformRunBlocking {
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.unlockedDeviceId,
            ipAddress = "5.6.7",
            pairingKey = MockNetworkClient.validPairingKey,
            lockState = LockState(MockNetworkClient.unlockedDeviceId, false)
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(device)
        presenter.api.client = MockNetworkClient()

        // before we start, is it unlocked?
        assertEquals(false, presenter.device.lockState?.isLocked)

        var initialHit = false
        val viewModel = presenter.lockAsync(
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertFalse(initialViewModel.lockButtonEnabled)
                assertFalse(initialViewModel.unlockButtonEnabled)
                assertTrue(initialViewModel.indicatorAnimating)
                assertNull(initialViewModel.errorMessage)
            },
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertFalse(viewModel.lockButtonEnabled)
        assertTrue(viewModel.unlockButtonEnabled)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.errorMessage)

        // Is the presenter device updated?
        assertEquals(true, presenter.device.lockState?.isLocked)

        // Was the device updated in the device list as well?
        val firstDevice = DeviceManager.loadPairedDevicesFromDatabase().first()
        firstDevice.lockState?.let {
            assertTrue(it.isLocked)
            assertEquals(device.deviceId, it.deviceId)
        } ?: fail("Couldn't get device lock state!")
    }

    @Test
    fun lockingUnlockedDeviceWithInvalidKeyFails() = platformRunBlocking {
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.unlockedDeviceId,
            ipAddress = "4.5.6",
            pairingKey = "LOL NO",
            lockState = LockState(MockNetworkClient.unlockedDeviceId, false)
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(device)
        presenter.api.client = MockNetworkClient()

        // before we start, is it unlocked?
        assertEquals(false, presenter.device.lockState?.isLocked)

        var initialHit = false
        val viewModel = presenter.lockAsync(
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertFalse(initialViewModel.lockButtonEnabled)
                assertFalse(initialViewModel.unlockButtonEnabled)
                assertTrue(initialViewModel.indicatorAnimating)
                assertNull(initialViewModel.errorMessage)
            },
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertTrue(viewModel.lockButtonEnabled)
        assertFalse(viewModel.unlockButtonEnabled)
        assertFalse(viewModel.indicatorAnimating)
        assertEquals("Invalid pairing key!", viewModel.errorMessage)

        // We should still think the device is unlocked.
        assertEquals(false, presenter.device.lockState?.isLocked)
        assertEquals(false, DeviceManager.loadPairedDevicesFromDatabase().first().lockState?.isLocked)
    }

    @Test
    fun unlockingLockedDeviceWithValidKeySucceeds() = platformRunBlocking {
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = MockNetworkClient.validPairingKey,
            lockState = LockState(MockNetworkClient.unlockedDeviceId, true)
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(device)
        presenter.api.client = MockNetworkClient()

        // before we start, is it locked?
        assertEquals(true, presenter.device.lockState?.isLocked)

        var initialHit = false
        val viewModel = presenter.unlockAsync(
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertFalse(initialViewModel.lockButtonEnabled)
                assertFalse(initialViewModel.unlockButtonEnabled)
                assertTrue(initialViewModel.indicatorAnimating)
                assertNull(initialViewModel.errorMessage)
            },
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertTrue(viewModel.lockButtonEnabled)
        assertFalse(viewModel.unlockButtonEnabled)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.errorMessage)

        // Is the presenter device updated?
        assertEquals(false, presenter.device.lockState?.isLocked)

        // Was the device updated in the device list as well?
        val firstDevice = DeviceManager.loadPairedDevicesFromDatabase().first()
        firstDevice.lockState?.let {
            assertFalse(it.isLocked)
            assertEquals(device.deviceId, it.deviceId)
        } ?: fail("Couldn't get device lock state!")
    }

    @Test
    fun unlockingLockedDeviceWithInvalidKeyFails() = platformRunBlocking {
        storage.tokenString = MockNetworkClient.mockToken

        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = "LOL NO",
            lockState = LockState(MockNetworkClient.lockedDeviceId, true)
        )

        DeviceManager.storeDeviceToDatabase(device)

        val presenter = DeviceDetailPresenter(device)
        presenter.api.client = MockNetworkClient()

        // before we start, is it locked?
        assertEquals(true, presenter.device.lockState?.isLocked)

        var initialHit = false
        val viewModel = presenter.unlockAsync(
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
            },
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertFalse(viewModel.lockButtonEnabled)
        assertTrue(viewModel.unlockButtonEnabled)
        assertFalse(viewModel.indicatorAnimating)
        assertEquals("Invalid pairing key!", viewModel.errorMessage)

        // We should still think the device is locked.
        assertEquals(true, presenter.device.lockState?.isLocked)
        assertEquals(true, DeviceManager.loadPairedDevicesFromDatabase().first().lockState?.isLocked)
    }
}
