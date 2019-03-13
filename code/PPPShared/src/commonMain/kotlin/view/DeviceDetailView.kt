package no.bakkenbaeck.pppshared.view

import no.bakkenbaeck.pppshared.interfaces.IndefiniteLoadingIndicating

interface DeviceDetailView: IndefiniteLoadingIndicating {

    fun setTitle(toString: String)
    fun setLockButtonEnabled(enabled: Boolean)
    fun setUnlockButtonEnabled(enabled: Boolean)

    fun setApiError(toString: String?)
}