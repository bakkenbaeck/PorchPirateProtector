package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import no.bakkenbaeck.porchpirateprotector.R

import kotlinx.android.synthetic.main.fragment_welcome.*
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.pppshared.presenter.WelcomePresenter

class WelcomeFragment: Fragment() {

    private val secureStorage by lazy { KeyStoreManager(context!!) }
    private val presenter = WelcomePresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_create_account.setOnClickListener { navigateToCreateAccount() }
        button_login.setOnClickListener { navigateToLogin() }

        if (presenter.skipWelcome(secureStorage = secureStorage)) {
            navigateToDeviceList()
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    // NAVIGATION

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
    }

    private fun navigateToCreateAccount() {
        findNavController().navigate(R.id.action_welcomeFragment_to_createAccountFragment)
    }

    private fun navigateToDeviceList() {
        findNavController().navigate(R.id.action_welcomeFragment_to_deviceListFragment)
    }
}