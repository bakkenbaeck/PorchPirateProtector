package no.bakkenbaeck.pppshared.view

import no.bakkenbaeck.pppshared.interfaces.IndefiniteLoadingIndicating

/**
 * Interface to be implemented per platform.
 */
interface LoginView: IndefiniteLoadingIndicating {

    /// The text the user has input as their email address.
    var email: String?

    /// The text the user has input as their password.
    var password: String?

    fun emailErrorUpdated(toString: String?)
    fun passwordErrorUpdated(toString: String?)
    fun apiErrorUpdated(toString: String?)

    fun setSubmitButtonEnabled(enabled: Boolean)

    /// Called when login completes successfully.
    fun loginSucceeded()
}