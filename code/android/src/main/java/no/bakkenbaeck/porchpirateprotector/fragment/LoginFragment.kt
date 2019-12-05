package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import no.bakkenbaeck.porchpirateprotector.R

import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.launch
import no.bakkenbaeck.porchpirateprotector.extension.hideSoftKeyboard
import no.bakkenbaeck.porchpirateprotector.extension.updateAnimating
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.pppshared.presenter.LoginPresenter


class LoginFragment: Fragment() {

    private val secureStorage: KeyStoreManager
        get() = KeyStoreManager(context!!)

    private val presenter = LoginPresenter()

    private var email: String?
        get() = text_input_username.editText?.text.toString()
        set(value) { text_input_username.editText?.setText(value) }

    private var password: String?
        get() = text_input_password.editText?.text.toString()
        set(value) { text_input_password.editText?.setText(value) }

    private fun handleFocusChange(forView: View, hasFocus: Boolean) {
        if (hasFocus) {
            // We just started editing, don't check yet.
            return
        }

        when (forView) {
            text_input_username.editText -> {
                text_input_username.error = presenter.validateEmail(email)
            }
            text_input_password.editText -> {
                text_input_password.error = presenter.validatePassword(password)
            }
        }
    }

    // FRAGMENT LIFECYCLE OVERRIDES

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_login_submit.setOnClickListener {
            presenter.launch {
                val viewModel = presenter.loginAsync(
                    email = email,
                    password = password,
                    initialViewModelHandler = this@LoginFragment::configureForViewModel,
                    secureStorage = secureStorage
                )

                configureForViewModel(viewModel)
            }
        }

        text_input_username.editText?.setOnFocusChangeListener(::handleFocusChange)
        text_input_password.editText?.setOnFocusChangeListener(::handleFocusChange)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    // VIEW MODEL CONFIGURATION

    private fun configureForViewModel(viewModel: LoginPresenter.LoginViewModel) {
        textview_error_login.text = viewModel.apiError
        button_login_submit.isEnabled = viewModel.submitButtonEnabled
        progress_bar_login.updateAnimating(viewModel.indicatorAnimating)

        text_input_username.error = viewModel.emailError
        text_input_password.error = viewModel.passwordError

        if (viewModel.loginSucceeded) {
            loginSucceeded()
        }

    }

    private fun loginSucceeded() {
        hideSoftKeyboard()
        findNavController().navigate(R.id.action_loginFragment_to_deviceListFragment)
    }
}