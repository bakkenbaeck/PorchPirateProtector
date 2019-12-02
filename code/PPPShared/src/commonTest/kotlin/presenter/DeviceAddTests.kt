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
    fun callingInitialViewModelPassesBackCorrectIPAddresses() {
        val initialList = listOf("1.2.3", "4.5.6")
        insecureStorage.storeIPAddresses(initialList)

        val viewModel = presenter.initialViewModel(insecureStorage)

        assertEquals(initialList, viewModel.availableIPAddresses)

        val updatedList = listOf("4.5.6")
        insecureStorage.storeIPAddresses(updatedList)

        val updatedViewModel = presenter.initialViewModel(insecureStorage)
        assertEquals(updatedList, updatedViewModel.availableIPAddresses)
    }

    @Test
    fun attemptingToAddDeviceWithIncorrectLoginTokenFails() = platformRunBlocking {
        storage.tokenString = "Nooooope"
        insecureStorage.storeIPAddresses(listOf(MockNetworkClient.lockedIP))

        presenter.api.client = MockNetworkClient()

        var initialHit = false
        val viewModel = presenter.addDeviceAsync(
            deviceIpAddress = MockNetworkClient.lockedIP,
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertTrue(initialViewModel.indicatorAnimating)
                assertFalse(initialViewModel.deviceAdded)
                assertNull(initialViewModel.errorMessage)
            },
            insecureStorage = insecureStorage,
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertFalse(viewModel.indicatorAnimating)
        assertFalse(viewModel.deviceAdded)
        assertEquals("Not authorized!", viewModel.errorMessage)
    }

    @Test
    fun addingLockedDeviceSucceeds() = platformRunBlocking {
        storage.tokenString = MockNetworkClient.mockToken
        insecureStorage.storeIPAddresses(listOf(MockNetworkClient.lockedIP))
        presenter.api.client = MockNetworkClient()

        var initialHit = false
        val viewModel =  presenter.addDeviceAsync(
            deviceIpAddress = MockNetworkClient.lockedIP,
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertEquals(listOf(MockNetworkClient.lockedIP), initialViewModel.availableIPAddresses)
                assertTrue(initialViewModel.indicatorAnimating)
                assertFalse(initialViewModel.deviceAdded)
                assertNull(initialViewModel.errorMessage)
            },
            insecureStorage = insecureStorage,
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertTrue(viewModel.availableIPAddresses.isEmpty())
        assertTrue(viewModel.deviceAdded)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.errorMessage)

        DeviceManager.loadPairedDevicesFromDatabase()
            .firstOrNull { it.deviceId == MockNetworkClient.lockedDeviceId }
            ?.let {
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
        storage.tokenString = MockNetworkClient.mockToken

        val unlockedIPAddress = MockNetworkClient.lockedIP + ".1"
        insecureStorage.storeIPAddresses(listOf(unlockedIPAddress))

        presenter.api.client = MockNetworkClient()

        var initialHit = false
        val viewModel = presenter.addDeviceAsync(
            deviceIpAddress = unlockedIPAddress,
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertEquals(listOf(unlockedIPAddress), initialViewModel.availableIPAddresses)
                assertTrue(initialViewModel.indicatorAnimating)
                assertFalse(initialViewModel.deviceAdded)
                assertNull(initialViewModel.errorMessage)
            },
            insecureStorage = insecureStorage,
            secureStorage = storage
        )

        assertTrue(initialHit)

        assertTrue(viewModel.availableIPAddresses.isEmpty())
        assertTrue(viewModel.deviceAdded)
        assertFalse(viewModel.indicatorAnimating)
        assertNull(viewModel.errorMessage)


        DeviceManager.loadPairedDevicesFromDatabase()
            .firstOrNull { it.deviceId == MockNetworkClient.unlockedDeviceId }
            ?.let {
                assertEquals(unlockedIPAddress, it.ipAddress)
                assertEquals(MockNetworkClient.validPairingKey, it.pairingKey)
                assertNotNull(it.lockState)
                assertEquals(false, it.lockState?.isLocked)
                assertEquals(listOf(it), DeviceManager.loadPairedDevicesFromDatabase())
                assertEquals(listOf<String>(), insecureStorage.loadIPAddresses())
            } ?: fail("Device was not successfully added!")
    }
}