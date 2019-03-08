package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import no.bakkenbaeck.porchpirateprotector.R

import kotlinx.android.synthetic.main.fragment_welcome.*
class WelcomeFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        button_create_account.setOnClickListener { createAccount() }
        button_login.setOnClickListener { login() }

        return view
    }

    private fun createAccount() {

    }

    private fun login() {

    }

}