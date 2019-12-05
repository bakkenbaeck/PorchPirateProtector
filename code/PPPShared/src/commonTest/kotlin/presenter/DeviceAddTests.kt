package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.platformRunBlocking
import no.bakkenbaeck.pppshared.mock.*
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.TestDb
import kotlin.test.*

class DeviceAddTests {

    val presenter = DeviceAddPresenter()
    val insecureStorage = MockInsecureStorage()
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
    fun callingInitialViewStatePassesBackCorrectIPAddresses() {
        val initialList = listOf("1.2.3", "4.5.6")
        insecureStorage.storeIPAddresses(initialList)

        val viewState = presenter.initialViewState(insecureStorage)

        assertEquals(initialList, viewState.availableIPAddresses)

        val updatedList = listOf("4.5.6")
        insecureStorage.storeIPAddresses(updatedList)

        val updatedViewState = presenter.initialViewState(insecureStorage)
        assertEquals(updatedList, updatedViewState.availableIPAddresses)
    }

    @Test
    fun attemptingToAddDeviceWithIncorrectLoginTokenFails() = platformRunBlocking {
        storage.tokenString = "Nooooope"
        insecureStorage.storeIPAddresses(listOf(MockNetworkClient.lockedIP, "4.5.6"))

        presenter.api.client = MockNetworkClient()

        var initialHit = false
        val viewState = presenter.addDeviceAsync(
            deviceIpAddress = MockNetworkClient.lockedIP,
            initialViewStateHandler = { initialViewState ->
                initialHit = true
                assertTrue(initialViewState.indicatorAnimating)
                assertFalse(initialViewState.deviceAdded)
                assertNull(initialViewState.errorMessage)
                assertEquals(listOf(MockNetworkClient.lockedIP, "4.5.6"), initialViewState.availableIPAddresses)
            },
            insecureStorage = insecureStorage,
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertEquals(listOf(MockNetworkClient.lockedIP, "4.5.6"), viewState.availableIPAddresses)
        assertFalse(viewState.indicatorAnimating)
        assertFalse(viewState.deviceAdded)
        assertEquals("Not authorized!", viewState.errorMessage)
    }

    @Test
    fun addingLockedDeviceSucceeds() = platformRunBlocking {
        storage.tokenString = MockNetworkClient.mockToken
        insecureStorage.storeIPAddresses(listOf(MockNetworkClient.lockedIP, "4.5.6"))
        presenter.api.client = MockNetworkClient()

        var initialHit = false
        val viewState =  presenter.addDeviceAsync(
            deviceIpAddress = MockNetworkClient.lockedIP,
            initialViewStateHandler = { initialViewState ->
                initialHit = true
                assertEquals(listOf(MockNetworkClient.lockedIP, "4.5.6"), initialViewState.availableIPAddresses)
                assertTrue(initialViewState.indicatorAnimating)
                assertFalse(initialViewState.deviceAdded)
                assertNull(initialViewState.errorMessage)
                assertNull(DeviceManager.loadPairedDevicesFromDatabase().firstOrNull())
            },
            insecureStorage = insecureStorage,
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertEquals(listOf("4.5.6"), viewState.availableIPAddresses)
        assertTrue(viewState.deviceAdded)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.errorMessage)

        DeviceManager.loadPairedDevicesFromDatabase()
            .firstOrNull { it.deviceId == MockNetworkClient.lockedDeviceId }
            ?.let {
                assertEquals(MockNetworkClient.lockedIP, it.ipAddress)
                assertEquals(MockNetworkClient.validPairingKey, it.pairingKey)
                assertNotNull(it.lockState)
                assertEquals(true, it.lockState?.isLocked)
                assertEquals(listOf(it), DeviceManager.loadPairedDevicesFromDatabase())
                assertEquals(listOf("4.5.6"), insecureStorage.loadIPAddresses())
            } ?: fail("Device was not successfully added!")
    }

    @Test
    fun addingUnlockedDeviceSucceeds() = platformRunBlocking {
        storage.tokenString = MockNetworkClient.mockToken

        val unlockedIPAddress = MockNetworkClient.lockedIP + ".1"
        insecureStorage.storeIPAddresses(listOf(unlockedIPAddress, "4.5.6"))

        presenter.api.client = MockNetworkClient()

        var initialHit = false
        val viewState = presenter.addDeviceAsync(
            deviceIpAddress = unlockedIPAddress,
            initialViewStateHandler = { initialViewState ->
                initialHit = true
                assertEquals(listOf(unlockedIPAddress, "4.5.6"), initialViewState.availableIPAddresses)
                assertTrue(initialViewState.indicatorAnimating)
                assertFalse(initialViewState.deviceAdded)
                assertNull(initialViewState.errorMessage)
            },
            insecureStorage = insecureStorage,
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertEquals(listOf("4.5.6"), viewState.availableIPAddresses)
        assertTrue(viewState.deviceAdded)
        assertFalse(viewState.indicatorAnimating)
        assertNull(viewState.errorMessage)


        DeviceManager.loadPairedDevicesFromDatabase()
            .firstOrNull { it.deviceId == MockNetworkClient.unlockedDeviceId }
            ?.let {
                assertEquals(unlockedIPAddress, it.ipAddress)
                assertEquals(MockNetworkClient.validPairingKey, it.pairingKey)
                assertNotNull(it.lockState)
                assertEquals(false, it.lockState?.isLocked)
                assertEquals(listOf(it), DeviceManager.loadPairedDevicesFromDatabase())
                assertEquals(listOf("4.5.6"), insecureStorage.loadIPAddresses())
            } ?: fail("Device was not successfully added!")
    }
}