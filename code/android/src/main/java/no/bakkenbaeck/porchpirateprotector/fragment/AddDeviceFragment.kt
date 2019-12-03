package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_device_add.*
import kotlinx.coroutines.launch
import no.bakkenbaeck.porchpirateprotector.R
import no.bakkenbaeck.porchpirateprotector.adapter.IpListAdapter
import no.bakkenbaeck.porchpirateprotector.adapter.IpSelectionListener
import no.bakkenbaeck.porchpirateprotector.extension.updateAnimating
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.porchpirateprotector.manager.SharedPreferencesManager
import no.bakkenbaeck.pppshared.presenter.DeviceAddPresenter

class AddDeviceFragment: Fragment(), IpSelectionListener {

    private val presenter = DeviceAddPresenter()
    private val insecureStorage by lazy { SharedPreferencesManager(context!!) }
    private val secureStorage by lazy { KeyStoreManager(context!!) }
    private val adapter by lazy { IpListAdapter(this) }

    // FRAGMENT LIFECYCLE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_device_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_ip_list.layoutManager = LinearLayoutManager(context)
        recyclerview_ip_list.adapter = adapter

        val initialViewModel = presenter.initialViewModel(insecureStorage)
        configureForViewModel(initialViewModel)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    // IP SELECTION LISTENER

    override fun selectedIpAddress(ipAddress: String) {
        presenter.launch {
            val viewModel = presenter.addDeviceAsync(
                deviceIpAddress = ipAddress,
                initialViewModelHandler = this@AddDeviceFragment::configureForViewModel,
                secureStorage = secureStorage,
                insecureStorage = insecureStorage
            )

            configureForViewModel(viewModel)
        }
    }

    // VIEW MODEL CONFIGURATION

    private fun configureForViewModel(viewModel: DeviceAddPresenter.DeviceAddViewModel) {
        adapter.updateAddresses(viewModel.availableIPAddresses)
        progress_bar_add_device.updateAnimating(viewModel.indicatorAnimating)

        viewModel.errorMessage?.let {
            Snackbar.make(coordinator_device_add, it, Snackbar.LENGTH_LONG).show()
        }

        if (viewModel.deviceAdded) {
            deviceAddedSuccessfully()
        }
    }

    private fun deviceAddedSuccessfully() {
        findNavController().popBackStack(R.id.deviceListFragment, false)
    }
}