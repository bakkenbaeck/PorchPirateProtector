package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import no.bakkenbaeck.porchpirateprotector.R
import no.bakkenbaeck.pppshared.view.CreateAccountView

class CreateAccountFragment: Fragment(), CreateAccountView {

    // FRAGMENT LIFECYCLE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    // CREATE ACCOUNT VIEW OVERRIDES

    override var email: String?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var password: String?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var confirmPassword: String?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun emailErrorUpdated(toString: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun passwordErrorUpdated(toString: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun confirmPasswordErrorUpdated(toString: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun accountSuccessfullyCreated() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun apiErrorUpdated(toString: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startLoadingIndicator() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stopLoadingIndicator() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}