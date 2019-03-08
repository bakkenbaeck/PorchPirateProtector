package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import no.bakkenbaeck.porchpirateprotector.R

import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_create_account.setOnClickListener { createAccount() }
        button_login.setOnClickListener { login() }
    }

    private fun createAccount() {
        findNavController().navigate(R.id.action_welcomeFragment_to_createAccountFragment)
    }

    private fun login() {
        findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
    }
}