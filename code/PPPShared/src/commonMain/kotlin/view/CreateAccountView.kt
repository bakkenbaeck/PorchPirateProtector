package no.bakkenbaeck.pppshared.view

import no.bakkenbaeck.pppshared.interfaces.ErrorHandling
import no.bakkenbaeck.pppshared.interfaces.IndefiniteLoadingIndicating

/**
 * Interface to be implemented per platform.
 */
interface CreateAccountView: ErrorHandling, IndefiniteLoadingIndicating {

    /// The text the user has input as their email address.
    var email: String?

    /// The text the user has input as their password.
    var password: String?

    /// The confirmed password for the user (which should match `password`).
    var confirmPassword: String?

    fun emailErrorUpdated(error: String?)
    fun passwordErrorUpdated(error: String?)
    fun confirmPasswordErrorUpdated(error: String?)

    /// Called when an account has been successfully created.
    fun accountSuccessfullyCreated()
}