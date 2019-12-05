package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import kotlinx.android.synthetic.main.fragment_device_list.*
import no.bakkenbaeck.pppshared.model.PairedDevice

import no.bakkenbaeck.porchpirateprotector.R
import no.bakkenbaeck.porchpirateprotector.adapter.DeviceListAdapter
import no.bakkenbaeck.porchpirateprotector.adapter.DeviceSelectionListener
import no.bakkenbaeck.porchpirateprotector.extension.updateAnimating
import no.bakkenbaeck.porchpirateprotector.fragment.DeviceDetailFragment.Companion.ARG_DEVICE
import no.bakkenbaeck.porchpirateprotector.manager.SharedPreferencesManager
import no.bakkenbaeck.pppshared.presenter.DeviceListPresenter

class DeviceListFragment: Fragment(), DeviceSelectionListener {

    private val adapter by lazy { DeviceListAdapter(this) }

    private val insecureStorage: SharedPreferencesManager
        get() = SharedPreferencesManager(this.context!!)

    private val presenter = DeviceListPresenter()

    // FRAGMENT LIFECYCLE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_device_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_device_list.layoutManager = LinearLayoutManager(context)
        recyclerview_device_list.adapter = this.adapter

        fab_add_device.setOnClickListener { showAddDevice() }
    }

    override fun onResume() {
        super.onResume()
        val viewState = presenter.updateViewState(insecureStorage)
        configureForViewState(viewState)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    // DEVICE SELECTION LISTENER

    override fun deviceSelected(device: PairedDevice) {
        showDetailForDevice(device)
    }

    // VIEW STATE CONFIGURATION

    private fun configureForViewState(viewState: DeviceListPresenter.DeviceListViewState) {
        adapter.list = viewState.pairedDeviceList
        progress_bar_device_list.updateAnimating(viewState.indicatorAnimating)
        fab_add_device.isEnabled = viewState.addButtonEnabled

        viewState.apiError?.let {
            Snackbar.make(coordinator_device_list, it, LENGTH_LONG).show()
        }
    }

    private fun showAddDevice() {
        findNavController().navigate(R.id.action_deviceListFragment_to_addDeviceFragment)
    }

    private fun showDetailForDevice(device: PairedDevice) {
        val bundle = Bundle().apply {
            putString(ARG_DEVICE, device.toJSONString())
        }

        findNavController().navigate(R.id.action_deviceListFragment_to_deviceDetailFragment, bundle)
    }

}