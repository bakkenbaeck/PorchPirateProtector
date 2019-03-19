package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.platformRunBlocking
import no.bakkenbaeck.pppshared.mock.*
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.view.DeviceAddView
import kotlin.test.*

class DeviceAddTests {

    class TestAddView: DeviceAddView {
        var ipAddresses: List<String>? = null
        override fun updatedAvailableDeviceIPAddresses(toList: List<String>) {
            ipAddresses = toList
        }

        var successfullyAddedDevice: PairedDevice? = null
        override fun deviceAddedSuccessfully(device: PairedDevice) {
            successfullyAddedDevice = device
        }

        var pairingError: String? = null
        override fun pairingErrorUpdated(toString: String?) {
            pairingError = toString
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
    fun callingUpdatePassesBackCorrectIPAddresses() {
        DeviceManager.unpairedDeviceIpAddresses = mutableListOf("1.2.3", "4.5.6")
        val expectedInitialList = listOf("1.2.3", "4.5.6")

        val view = TestAddView()
        val storage = MockStorage()
        storage.tokenString = "MOCK TOKEN"
        val presenter = DeviceAddPresenter(view, storage)

        assertNull(view.ipAddresses)

        presenter.updateAvailableIPAddresses()

        assertNotNull(view.ipAddresses)
        assertEquals(expectedInitialList, view.ipAddresses)

        DeviceManager.unpairedDeviceIpAddresses.removeAt(0)

        // Actual stuff on the view shouldn't have changed yet
        assertEquals(expectedInitialList, view.ipAddresses)

        presenter.updateAvailableIPAddresses()

        assertNotEquals(expectedInitialList, view.ipAddresses)
        assertEquals(mutableListOf("4.5.6"), view.ipAddresses)
    }

    @Test
    fun attemptingToAddDeviceWithIncorrectLoginTokenFails() = platformRunBlocking {
        val view = TestAddView()
        val storage = MockStorage()
        storage.tokenString = "Nooooope"

        DeviceManager.unpairedDeviceIpAddresses = mutableListOf(MockNetworkClient.lockedIP)
        DeviceManager.pairedDevices = mutableListOf()

        val presenter = DeviceAddPresenter(view, storage)
        presenter.api.client = MockNetworkClient()

        presenter.addDeviceAsync(MockNetworkClient.lockedIP)

        assertFalse(view.loadingIndicatorGoing)
        assertTrue(view.loadingIndicatorStarted)
        assertTrue(view.loadingIndicatorStopped)
        assertNull(view.successfullyAddedDevice)
        assertEquals("Not authorized!", view.pairingError)
    }

    @Test
    fun addingLockedDeviceSucceeds() = platformRunBlocking {
        val view = TestAddView()
        val storage = MockStorage()
        storage.tokenString = MockNetworkClient.mockToken

        DeviceManager.unpairedDeviceIpAddresses = mutableListOf(MockNetworkClient.lockedIP)
        DeviceManager.pairedDevices = mutableListOf()

        val presenter = DeviceAddPresenter(view, storage)
        presenter.api.client = MockNetworkClient()

        presenter.addDeviceAsync(MockNetworkClient.lockedIP)

        assertFalse(view.loadingIndicatorGoing)
        assertTrue(view.loadingIndicatorStarted)
        assertTrue(view.loadingIndicatorStopped)

        view.successfullyAddedDevice?.let {
            assertEquals(MockNetworkClient.lockedDeviceId, it.deviceId)
            assertEquals(MockNetworkClient.lockedIP, it.ipAddress)
            assertEquals(MockNetworkClient.validPairingKey, it.pairingKey)
            assertNotNull(it.lockState)
            assertEquals(true, it.lockState?.isLocked)
            assertEquals(listOf(it), DeviceManager.pairedDevices)
            assertEquals(listOf<String>(), DeviceManager.unpairedDeviceIpAddresses)
        } ?: fail("Device was not successfully added!")
    }

    @Test
    fun addingUnlockedDeviceSucceeds() = platformRunBlocking {
        val view = TestAddView()
        val storage = MockStorage()
        storage.tokenString = MockNetworkClient.mockToken

        val unlockedIPAddress = MockNetworkClient.lockedIP + ".1"

        DeviceManager.unpairedDeviceIpAddresses = mutableListOf(unlockedIPAddress)
        DeviceManager.pairedDevices = mutableListOf()

        val presenter = DeviceAddPresenter(view, storage)
        presenter.api.client = MockNetworkClient()

        presenter.addDeviceAsync(unlockedIPAddress)

        assertFalse(view.loadingIndicatorGoing)
        assertTrue(view.loadingIndicatorStarted)
        assertTrue(view.loadingIndicatorStopped)

        view.successfullyAddedDevice?.let {
            assertEquals(MockNetworkClient.unlockedDeviceId, it.deviceId)
            assertEquals(unlockedIPAddress, it.ipAddress)
            assertEquals(MockNetworkClient.validPairingKey, it.pairingKey)
            assertNotNull(it.lockState)
            assertEquals(false, it.lockState?.isLocked)
            assertEquals(listOf(it), DeviceManager.pairedDevices)
            assertEquals(listOf<String>(), DeviceManager.unpairedDeviceIpAddresses)
        } ?: fail("Device was not successfully added!")
    }
}