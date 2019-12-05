package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_create_account.*
import kotlinx.coroutines.launch
import no.bakkenbaeck.porchpirateprotector.R
import no.bakkenbaeck.porchpirateprotector.extension.*
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.pppshared.presenter.CreateAccountPresenter

class CreateAccountFragment: Fragment() {

    private val presenter = CreateAccountPresenter()
    private val secureStorage: KeyStoreManager
        get() = KeyStoreManager(context!!)

    private var email: String?
        get() = text_input_username.editText?.text.toString()
        set(value) { text_input_username.editText?.setText(value) }

    private var password: String?
        get() = text_input_password.editText?.text.toString()
        set(value) { text_input_password.editText?.setText(value) }

    private var confirmPassword: String?
        get() = text_input_confirm_password.editText?.text.toString()
        set(value) { text_input_confirm_password.editText?.setText(value) }

    private fun handleFocusChange(forView: View, hasFocus: Boolean) {
        if (hasFocus) {
            // We just started editing, don't check yet.
            return
        }

        when (forView) {
            text_input_username.editText -> presenter.validateEmail(email)
            text_input_password.editText -> presenter.validatePassword(password)
            text_input_confirm_password.editText -> presenter.validateConfirmPassword(password, confirmPassword)
        }
    }

    // FRAGMENT LIFECYCLE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_create_account_submit.setOnClickListener {
            presenter.launch {
                val viewState = presenter.createAccountAsync(
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    initialViewStateHandler = this@CreateAccountFragment::configureForViewState,
                    secureStorage = secureStorage
                )

                configureForViewState(viewState)
            }
        }

        text_input_username.editText?.setOnFocusChangeListener(::handleFocusChange)
        text_input_password.editText?.setOnFocusChangeListener(::handleFocusChange)
        text_input_confirm_password.editText?.setOnFocusChangeListener(::handleFocusChange)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    // VIEW STATE CONFIGURATION

    private fun configureForViewState(viewState: CreateAccountPresenter.CreateAccountViewState) {
        text_input_username.error = viewState.emailError
        text_input_password.error = viewState.passwordError
        text_input_confirm_password.error = viewState.confirmPasswordError
        textview_error_create_account.text = viewState.apiErrorMessage
        button_create_account_submit.isEnabled = viewState.submitButtonEnabled
        progress_bar_create_account.updateAnimating(viewState.indicatorAnimating)

        if (viewState.accountCreated) {
            accountSuccessfullyCreated()
        }
    }

    private fun accountSuccessfullyCreated() {
        hideSoftKeyboard()
        findNavController().navigate(R.id.action_createAccountFragment_to_deviceListFragment)
    }
}