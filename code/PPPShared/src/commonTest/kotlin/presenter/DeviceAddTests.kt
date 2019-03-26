package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.platformRunBlocking
import no.bakkenbaeck.pppshared.mock.*
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.view.DeviceAddView
import no.bakkenbaeck.pppshared.TestDb
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

    @BeforeTest
    fun setup() {
        TestDb.setupIfNeeded()
    }

    @AfterTest
    fun tearDown(){
        TestDb.clearDatabase()
    }

    @Test
    fun callingUpdatePassesBackCorrectIPAddresses() {
        val insecureStorage = MockInsecureStorage()
        val initialList = listOf("1.2.3", "4.5.6")
        insecureStorage.storeIPAddresses(initialList)

        val view = TestAddView()
        val storage = MockSecureStorage()
        storage.tokenString = "MOCK TOKEN"

        val presenter = DeviceAddPresenter(view, storage, insecureStorage)

        assertNull(view.ipAddresses)

        presenter.updateAvailableIPAddresses()

        assertNotNull(view.ipAddresses)
        assertEquals(initialList, view.ipAddresses)

        val updatedList = listOf("4.5.6")
        insecureStorage.storeIPAddresses(updatedList)

        // Actual stuff on the view shouldn't have changed yet
        assertEquals(initialList, view.ipAddresses)

        presenter.updateAvailableIPAddresses()

        assertNotEquals(initialList, view.ipAddresses)
        assertEquals(updatedList, view.ipAddresses)
    }

    @Test
    fun attemptingToAddDeviceWithIncorrectLoginTokenFails() = platformRunBlocking {
        val view = TestAddView()
        val storage = MockSecureStorage()
        storage.tokenString = "Nooooope"

        val insecureStorage = MockInsecureStorage()
        insecureStorage.storeIPAddresses(listOf(MockNetworkClient.lockedIP))

        val presenter = DeviceAddPresenter(view, storage, insecureStorage)
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
        val storage = MockSecureStorage()
        storage.tokenString = MockNetworkClient.mockToken

        val insecureStorage = MockInsecureStorage()
        insecureStorage.storeIPAddresses(listOf(MockNetworkClient.lockedIP))

        val presenter = DeviceAddPresenter(view, storage, insecureStorage)
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
            assertEquals(listOf(it), DeviceManager.loadPairedDevicesFromDatabase())
            assertEquals(listOf<String>(), insecureStorage.loadIPAddresses())
        } ?: fail("Device was not successfully added!")
    }

    @Test
    fun addingUnlockedDeviceSucceeds() = platformRunBlocking {
        val view = TestAddView()
        val storage = MockSecureStorage()
        storage.tokenString = MockNetworkClient.mockToken

        val unlockedIPAddress = MockNetworkClient.lockedIP + ".1"

        val insecureStorage = MockInsecureStorage()
        insecureStorage.storeIPAddresses(listOf(unlockedIPAddress))

        val presenter = DeviceAddPresenter(view, storage, insecureStorage)
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
            assertEquals(listOf(it), DeviceManager.loadPairedDevicesFromDatabase())
            assertEquals(listOf<String>(), insecureStorage.loadIPAddresses())
        } ?: fail("Device was not successfully added!")
    }
}