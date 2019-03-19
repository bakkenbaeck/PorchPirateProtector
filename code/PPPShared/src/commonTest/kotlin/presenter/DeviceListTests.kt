package no.bakkenbaeck.pppshared

import no.bakkenbaeck.pppshared.manager.DeviceManager
import no.bakkenbaeck.pppshared.manager.TokenManager
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.model.UserToken
import no.bakkenbaeck.pppshared.presenter.DeviceListPresenter
import no.bakkenbaeck.pppshared.view.DeviceListView
import kotlin.test.*

class DeviceListTests {

    class TestDeviceListView: DeviceListView {
        var addEnabled = false
        override fun setAddButtonEnabled(enabled: Boolean) {
            addEnabled = enabled
        }

        var deviceList: List<PairedDevice>? = null
        override fun deviceListUpdated(toDeviceList: List<PairedDevice>) {
            deviceList = toDeviceList
        }

        var selectedDevice: PairedDevice? = null
        override fun showDetailForDevice(device: PairedDevice) {
            selectedDevice = device
        }

        var apiError: String? = null
        override fun apiErrorUpdated(toString: String?) {
            apiError = toString
        }

        var loadingIndicatorStarted = false
        var loadingIndicatorGoing = false
        override fun startLoadingIndicator() {
            loadingIndicatorGoing = true
            loadingIndicatorStarted = true
        }

        var loadingIndicatorStopped = false
        override fun stopLoadingIndicator() {
            loadingIndicatorGoing = false
            loadingIndicatorStopped = true
        }

        var navigatedToAdd = false
        override fun showAddDevice() {
            navigatedToAdd = true
        }
    }

    @Test
    fun updatingDeviceListSetsProperListAndEnablesOrDisablesButton() {
        val mockIPAddresses = listOf("1.2.3", "4.5.6")

        DeviceManager.unpairedDeviceIpAddresses = mockIPAddresses.toMutableList()
        DeviceManager.pairedDevices = mutableListOf()

        val view = TestDeviceListView()
        val storage = MockStorage()
        storage.storeTokenString("TESTING_TOKEN")
        val presenter = DeviceListPresenter(view, storage)

        presenter.updateDeviceList()

        assertNotNull(view.deviceList)
        assertEquals(view.deviceList?.isEmpty(), true)
        assertNull(view.apiError)
        assertTrue(view.addEnabled)

        val fakeDevice = PairedDevice(1,
            mockIPAddresses.first(),
            "fake_pairing_key",
            null)

        DeviceManager.pairedDevices.add(fakeDevice)
        DeviceManager.unpairedDeviceIpAddresses.removeAt(0)

        presenter.updateDeviceList()

        assertNotNull(view.deviceList)
        assertEquals(view.deviceList?.count(), 1)
        assertNull(view.apiError)

        assertFalse(view.navigatedToAdd)
        assertFalse(view.loadingIndicatorStarted)
        assertFalse(view.loadingIndicatorStopped)
        assertFalse(view.loadingIndicatorGoing)
    }

    @Test
    fun fetchingDeviceDetails() = platformRunBlocking {
        val view = TestDeviceListView()
        val storage = MockStorage()
        storage.storeTokenString(MockNetworkClient.mockToken)
        val presenter = DeviceListPresenter(view, storage)
        presenter.api.client = MockNetworkClient()

        DeviceManager.pairedDevices = mutableListOf()
        DeviceManager.unpairedDeviceIpAddresses = mutableListOf(MockNetworkClient.lockedIP)

        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = MockNetworkClient.validPairingKey,
            lockState = null)

        assertFalse(view.loadingIndicatorStarted)
        assertFalse(view.loadingIndicatorStopped)
        assertFalse(view.loadingIndicatorGoing)

        val fetched = presenter.fetchDeviceDetailsAsync(device)

        assertTrue(view.loadingIndicatorStarted)
        assertTrue(view.loadingIndicatorStopped)
        assertFalse(view.loadingIndicatorGoing)

        assertEquals(1, fetched?.count())
        assertNull(view.apiError)

        view.deviceList?.let { devices ->
            assertEquals(fetched, devices)
            assertEquals(1, devices.count())
            val firstDevice = devices.first()
            assertEquals(device.deviceId, firstDevice.deviceId)
            assertEquals(device.ipAddress, firstDevice.ipAddress)
            assertEquals(device.pairingKey, firstDevice.pairingKey)
            assertNotNull(firstDevice.lockState)
            assertEquals(true, firstDevice.lockState?.isLocked)
        } ?: fail("Device list was null")

        assertTrue(view.addEnabled)
        assertFalse(view.navigatedToAdd)
    }

    @Test
    fun selectingADeviceTellsTheViewToNavigateToIt() {
        val device = PairedDevice(
            deviceId = MockNetworkClient.lockedDeviceId,
            ipAddress = MockNetworkClient.lockedIP,
            pairingKey = MockNetworkClient.validPairingKey,
            lockState = null)

        val view = TestDeviceListView()
        val storage = MockStorage()
        storage.storeTokenString("TESTING_TOKEN")
        val presenter = DeviceListPresenter(view, storage)

        assertNull(view.selectedDevice)

        presenter.selectedDevice(device)

        assertNotNull(view.selectedDevice)
        assertEquals(device, view.selectedDevice)
    }

    @Test
    fun selectingAddButtonTellsViewToNavigateToAdd() {
        val view = TestDeviceListView()
        val storage = MockStorage()
        storage.storeTokenString("TESTING_TOKEN")
        val presenter = DeviceListPresenter(view, storage)

        assertFalse(view.navigatedToAdd)

        presenter.selectedAddDevice()

        assertTrue(view.navigatedToAdd)
    }
}