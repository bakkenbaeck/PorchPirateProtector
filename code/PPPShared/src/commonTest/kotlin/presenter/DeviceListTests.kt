package no.bakkenbaeck.pppshared.presenter

import no.bakkenbaeck.pppshared.TestDb
import no.bakkenbaeck.pppshared.platformRunBlocking
import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.mock.*
import no.bakkenbaeck.pppshared.model.PairedDevice
import kotlin.test.*

class DeviceListTests {

    val insecureStorage = MockInsecureStorage()
    val storage = MockSecureStorage()
    val presenter = DeviceListPresenter()

    @BeforeTest
    fun setup() {
        TestDb.setupIfNeeded()
    }

    @AfterTest
    fun tearDown(){
        TestDb.clearDatabase()
    }


    @Test
    fun updatingDeviceListSetsProperListAndEnablesOrDisablesButton() {
        val mockIPAddresses = listOf("1.2.3", "4.5.6")

        insecureStorage.storeIPAddresses(mockIPAddresses)
        storage.storeTokenString("TESTING_TOKEN")

        val viewModel = presenter.updateViewModel(
            insecureStorage = insecureStorage,
            isLoading = false
        )

        assertNotNull(viewModel.pairedDeviceList)
        assertTrue(viewModel.pairedDeviceList.isEmpty())
        assertNull(viewModel.apiError)
        assertFalse(viewModel.indicatorAnimating)
        assertTrue(viewModel.addButtonEnabled)

        val addressToAdd = mockIPAddresses.first()

        val fakeDevice = PairedDevice(1,
            addressToAdd,
            "fake_pairing_key",
            null)


        DeviceManager.storeDeviceToDatabase(fakeDevice)
        insecureStorage.removeIPAddress(addressToAdd)

        val updatedViewModel = presenter.updateViewModel(
            insecureStorage = insecureStorage,
            isLoading = false
        )

        assertNotNull(updatedViewModel.pairedDeviceList)
        assertEquals(updatedViewModel.pairedDeviceList.count(), 1)
        assertEquals(updatedViewModel.pairedDeviceList, listOf(fakeDevice))
        assertNull(updatedViewModel.apiError)
        assertFalse(updatedViewModel.indicatorAnimating)
        assertTrue(updatedViewModel.addButtonEnabled)
    }

    @Test
    fun fetchingDeviceDetails() = platformRunBlocking {
        storage.storeTokenString(MockNetworkClient.mockToken)
        presenter.api.client = MockNetworkClient()

        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = MockNetworkClient.validPairingKey,
            lockState = null
        )
        DeviceManager.storeDeviceToDatabase(device)

        var initialHit = false
        val viewModel = presenter.fetchDeviceDetailsAsync(
            device = device,
            initialViewModelHandler = { initialViewModel ->
                initialHit = true
                assertEquals(listOf(device), initialViewModel.pairedDeviceList)
                assertTrue(initialViewModel.indicatorAnimating)
                assertNull(initialViewModel.apiError)
                assertFalse(initialViewModel.addButtonEnabled)
            },
            secureStorage = storage,
            insecureStorage = insecureStorage
        )

        assertTrue(initialHit)

        assertFalse(viewModel.indicatorAnimating)
        assertFalse(viewModel.addButtonEnabled)
        assertNull(viewModel.apiError)


        val firstDevice = viewModel.pairedDeviceList.first()
        assertEquals(device.deviceId, firstDevice.deviceId)
        assertEquals(device.ipAddress, firstDevice.ipAddress)
        assertEquals(device.pairingKey, firstDevice.pairingKey)
        assertNotNull(firstDevice.lockState)
        assertEquals(true, firstDevice.lockState?.isLocked)
    }

}