package no.bakkenbaeck.pppshared.view

import no.bakkenbaeck.pppshared.interfaces.ErrorHandling
import no.bakkenbaeck.pppshared.interfaces.IndefiniteLoadingIndicating

interface LoginView: ErrorHandling, IndefiniteLoadingIndicating {

    /// The text the user has input as their email address.
    var email: String?

    /// The text the user has input as their password.
    var password: String?

    fun emailErrorUpdated(to: String?)
    fun passwordErrorUpdated(to: String?)

    /// Called when login completes successfully.
    fun loginSucceeded()
}