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

        val viewState = presenter.updateViewState(
            insecureStorage = insecureStorage,
            isLoading = false
        )

        assertNotNull(viewState.pairedDeviceList)
        assertTrue(viewState.pairedDeviceList.isEmpty())
        assertNull(viewState.apiError)
        assertFalse(viewState.indicatorAnimating)
        assertTrue(viewState.addButtonEnabled)

        val addressToAdd = mockIPAddresses.first()

        val fakeDevice = PairedDevice(1,
            addressToAdd,
            "fake_pairing_key",
            null)


        DeviceManager.storeDeviceToDatabase(fakeDevice)
        insecureStorage.removeIPAddress(addressToAdd)

        val updatedViewState = presenter.updateViewState(
            insecureStorage = insecureStorage,
            isLoading = false
        )

        assertNotNull(updatedViewState.pairedDeviceList)
        assertEquals(updatedViewState.pairedDeviceList.count(), 1)
        assertEquals(updatedViewState.pairedDeviceList, listOf(fakeDevice))
        assertNull(updatedViewState.apiError)
        assertFalse(updatedViewState.indicatorAnimating)
        assertTrue(updatedViewState.addButtonEnabled)
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
        val viewState = presenter.fetchDeviceDetailsAsync(
            device = device,
            initialViewStateHandler = { initialViewState ->
                initialHit = true
                assertEquals(listOf(device), initialViewState.pairedDeviceList)
                assertTrue(initialViewState.indicatorAnimating)
                assertNull(initialViewState.apiError)
                assertFalse(initialViewState.addButtonEnabled)
            },
            secureStorage = storage,
            insecureStorage = insecureStorage
        )

        assertTrue(initialHit)

        assertFalse(viewState.indicatorAnimating)
        assertFalse(viewState.addButtonEnabled)
        assertNull(viewState.apiError)


        val firstDevice = viewState.pairedDeviceList.first()
        assertEquals(device.deviceId, firstDevice.deviceId)
        assertEquals(device.ipAddress, firstDevice.ipAddress)
        assertEquals(device.pairingKey, firstDevice.pairingKey)
        assertNotNull(firstDevice.lockState)
        assertEquals(true, firstDevice.lockState?.isLocked)
    }

}