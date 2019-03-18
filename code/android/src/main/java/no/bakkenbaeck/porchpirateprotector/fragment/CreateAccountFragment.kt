package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_create_account.*
import no.bakkenbaeck.porchpirateprotector.R
import no.bakkenbaeck.porchpirateprotector.extension.hideSoftKeyboard
import no.bakkenbaeck.porchpirateprotector.extension.showAndStartAnimating
import no.bakkenbaeck.porchpirateprotector.extension.stopAnimatingAndHide
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.pppshared.presenter.CreateAccountPresenter
import no.bakkenbaeck.pppshared.view.CreateAccountView

class CreateAccountFragment: Fragment(), CreateAccountView {

    private val presenter by lazy { CreateAccountPresenter(this, KeyStoreManager) }

    private fun handleFocusChange(forView: View, hasFocus: Boolean) {
        if (hasFocus) {
            // We just started editing, don't check yet.
            return
        }

        when (forView) {
            text_input_username.editText -> presenter.validateEmail()
            text_input_password.editText -> presenter.validatePassword()
            text_input_confirm_password.editText -> presenter.validateConfirmPassword()
        }
    }

    // FRAGMENT LIFECYCLE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_create_account_submit.setOnClickListener { presenter.createAccount() }
        text_input_username.editText?.setOnFocusChangeListener(::handleFocusChange)
        text_input_password.editText?.setOnFocusChangeListener(::handleFocusChange)
        text_input_confirm_password.editText?.setOnFocusChangeListener(::handleFocusChange)
    }

    // CREATE ACCOUNT VIEW OVERRIDES

    override var email: String?
        get() = text_input_username.editText?.text.toString()
        set(value) { text_input_username.editText?.setText(value) }

    override var password: String?
        get() = text_input_password.editText?.text.toString()
        set(value) { text_input_password.editText?.setText(value) }

    override var confirmPassword: String?
        get() = text_input_confirm_password.editText?.text.toString()
        set(value) { text_input_confirm_password.editText?.setText(value) }

    override fun emailErrorUpdated(toString: String?) {
        text_input_username.error = toString
    }

    override fun passwordErrorUpdated(toString: String?) {
        text_input_password.error = toString
    }

    override fun confirmPasswordErrorUpdated(toString: String?) {
        text_input_confirm_password.error = toString
    }

    override fun accountSuccessfullyCreated() {
        hideSoftKeyboard()
        findNavController().navigate(R.id.action_createAccountFragment_to_deviceListFragment)
    }

    override fun apiErrorUpdated(toString: String?) {
        textview_error_create_account.text = toString
    }

    override fun startLoadingIndicator() {
        button_create_account_submit.isEnabled = false
        progress_bar_create_account.showAndStartAnimating()
    }

    override fun stopLoadingIndicator() {
        button_create_account_submit.isEnabled = true
        progress_bar_create_account.stopAnimatingAndHide()
    }
}