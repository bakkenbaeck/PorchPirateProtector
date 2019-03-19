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
import no.bakkenbaeck.pppshared.view.DeviceListView

import no.bakkenbaeck.porchpirateprotector.R
import no.bakkenbaeck.porchpirateprotector.adapter.DeviceListAdapter
import no.bakkenbaeck.porchpirateprotector.adapter.DeviceSelectionListener
import no.bakkenbaeck.porchpirateprotector.extension.showAndStartAnimating
import no.bakkenbaeck.porchpirateprotector.extension.stopAnimatingAndHide
import no.bakkenbaeck.porchpirateprotector.fragment.DeviceDetailFragment.Companion.ARG_DEVICE
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.pppshared.presenter.DeviceListPresenter

class DeviceListFragment: Fragment(), DeviceListView, DeviceSelectionListener {

    private val adapter by lazy { DeviceListAdapter(this) }
    private val presenter by lazy { DeviceListPresenter(this, KeyStoreManager) }

    // FRAGMENT LIFECYCLE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_device_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_device_list.layoutManager = LinearLayoutManager(context)
        recyclerview_device_list.adapter = this.adapter

        fab_add_device.setOnClickListener { presenter.selectedAddDevice() }
    }

    override fun onResume() {
        super.onResume()
        presenter.updateDeviceList()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    // DEVICE SELECTION LISTENER

    override fun deviceSelected(device: PairedDevice) {
        presenter.selectedDevice(device)
    }

    // DEVICE LIST VIEW

    override fun setAddButtonEnabled(enabled: Boolean) {
        fab_add_device.isEnabled = enabled
    }

    override fun showAddDevice() {
        findNavController().navigate(R.id.action_deviceListFragment_to_addDeviceFragment)
    }

    override fun deviceListUpdated(toDeviceList: List<PairedDevice>) {
        adapter.list = toDeviceList
    }

    override fun showDetailForDevice(device: PairedDevice) {
        val bundle = Bundle().apply {
            putString(ARG_DEVICE, device.toJSONString())
        }

        findNavController().navigate(R.id.action_deviceListFragment_to_deviceDetailFragment, bundle)
    }

    override fun apiErrorUpdated(toString: String?) {
        toString?.let {
            Snackbar.make(coordinator_device_list, it, LENGTH_LONG).show()
        }
    }

    override fun startLoadingIndicator() {
        progress_bar_device_list.showAndStartAnimating()
    }

    override fun stopLoadingIndicator() {
        progress_bar_device_list.stopAnimatingAndHide()
    }
}