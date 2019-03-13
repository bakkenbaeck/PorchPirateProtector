package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import no.bakkenbaeck.porchpirateprotector.R

import kotlinx.android.synthetic.main.fragment_login.*
import no.bakkenbaeck.porchpirateprotector.extension.hideSoftKeyboard
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.pppshared.presenter.LoginPresenter
import no.bakkenbaeck.pppshared.view.LoginView


class LoginFragment: Fragment(), LoginView {

    private val presenter by lazy { LoginPresenter(this, KeyStoreManager) }

    private fun handleFocusChange(forView: View, hasFocus: Boolean) {
        if (hasFocus) {
            // We just started editing, don't check yet.
            return
        }

        when (forView) {
            text_input_username.editText -> presenter.validateEmail()
            text_input_password.editText -> presenter.validatePassword()
        }
    }

    // FRAGMENT LIFECYCLE OVERRIDES

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_login_submit.setOnClickListener { presenter.login() }
        text_input_username.editText?.setOnFocusChangeListener(::handleFocusChange)
        text_input_password.editText?.setOnFocusChangeListener(::handleFocusChange)
    }

    // LOGIN VIEW

    override var email: String?
        get() = text_input_username.editText?.text.toString()
        set(value) { text_input_username.editText?.setText(value) }

    override var password: String?
        get() = text_input_password.editText?.text.toString()
        set(value) { text_input_password.editText?.setText(value) }

    override fun emailErrorUpdated(toString: String?) {
        text_input_username.error = toString
    }

    override fun apiErrorUpdated(toString: String?) {
        textview_error_login.text = toString
    }

    override fun loginSucceeded() {
        hideSoftKeyboard()
        findNavController().navigate(R.id.action_loginFragment_to_deviceListFragment)
    }

    override fun passwordErrorUpdated(toString: String?) {
        text_input_password.error = toString
    }

    override fun startLoadingIndicator() {
        progress_bar_login.visibility = View.VISIBLE
        progress_bar_login.animate()
        button_login_submit.isEnabled = false
    }

    override fun stopLoadingIndicator() {
        progress_bar_login.clearAnimation()
        progress_bar_login.visibility = View.GONE
        button_login_submit.isEnabled = true
    }
}