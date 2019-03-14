package no.bakkenbaeck.pppshared.view

import no.bakkenbaeck.pppshared.interfaces.IndefiniteLoadingIndicating
import no.bakkenbaeck.pppshared.model.PairedDevice

interface DeviceListView: IndefiniteLoadingIndicating {

    fun setAddButtonEnabled(enabled: Boolean)

    fun showAddDevice()

    fun deviceListUpdated(toDeviceList: List<PairedDevice>)

    fun showDetailForDevice(device: PairedDevice)

    fun apiErrorUpdated(toString: String?)
}