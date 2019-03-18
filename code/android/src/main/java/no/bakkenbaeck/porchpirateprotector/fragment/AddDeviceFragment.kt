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
import no.bakkenbaeck.porchpirateprotector.R
import no.bakkenbaeck.porchpirateprotector.adapter.IpListAdapter
import no.bakkenbaeck.porchpirateprotector.adapter.IpSelectionListener
import no.bakkenbaeck.porchpirateprotector.extension.showAndStartAnimating
import no.bakkenbaeck.porchpirateprotector.extension.stopAnimatingAndHide
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.presenter.DeviceAddPresenter
import no.bakkenbaeck.pppshared.view.DeviceAddView

class AddDeviceFragment: Fragment(), DeviceAddView, IpSelectionListener {

    private val presenter by lazy { DeviceAddPresenter(this, KeyStoreManager) }
    private val adapter by lazy { IpListAdapter(this) }

    // FRAGMENT LIFECYCLE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_device_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_ip_list.layoutManager = LinearLayoutManager(context)
        recyclerview_ip_list.adapter = adapter

        presenter.updateAvailableIPAddresses()
    }

    override fun selectedIpAddress(ipAddress: String) {
        presenter.addDevice(ipAddress)
    }

    // DEVICE ADD VIEW

    override fun updatedAvailableDeviceIPAddresses(toList: List<String>) {
        adapter.ipAddresses = toList
    }

    override fun deviceAddedSuccessfully(device: PairedDevice) {
        findNavController().popBackStack(R.id.deviceListFragment, false)
    }

    override fun pairingErrorUpdated(toString: String?) {
        toString?.let {
            Snackbar.make(coordinator_device_add, it, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun startLoadingIndicator() {
        progress_bar_add_device.showAndStartAnimating()
    }

    override fun stopLoadingIndicator() {
        progress_bar_add_device.stopAnimatingAndHide()
    }
}