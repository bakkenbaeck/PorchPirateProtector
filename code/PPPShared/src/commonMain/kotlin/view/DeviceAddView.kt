package no.bakkenbaeck.pppshared.view

import no.bakkenbaeck.pppshared.interfaces.IndefiniteLoadingIndicating
import no.bakkenbaeck.pppshared.model.PairedDevice

interface DeviceAddView: IndefiniteLoadingIndicating {

    fun updatedAvailableDeviceIPAddresses(toList: List<String>)
    fun deviceAddedSuccessfully(device: PairedDevice)
    fun pairingErrorUpdated(toString: String?)
}