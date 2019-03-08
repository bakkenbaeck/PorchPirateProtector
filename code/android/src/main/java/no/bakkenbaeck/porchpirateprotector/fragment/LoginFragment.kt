package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import no.bakkenbaeck.porchpirateprotector.R

import kotlinx.android.synthetic.main.fragment_login.*
import no.bakkenbaeck.pppshared.presenter.LoginPresenter
import no.bakkenbaeck.pppshared.view.LoginView

class LoginFragment: Fragment(), LoginView {

    private val presenter by lazy { LoginPresenter(this) }

    private fun login() {
        presenter.login()
    }

    // FRAGMENT LIFECYCLE OVERRIDES

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        button_login_submit.setOnClickListener { login() }

        return view
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
        println("SUCCESS")
    }

    override fun passwordErrorUpdated(toString: String?) {
        text_input_password.error = toString

    }

    override fun startLoadingIndicator() {
        progress_bar_login.visibility = View.VISIBLE
        progress_bar_login.animate()

    }

    override fun stopLoadingIndicator() {
        progress_bar_login.clearAnimation()
        progress_bar_login.visibility = View.GONE
    }
}