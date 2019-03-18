package no.bakkenbaeck.porchpirateprotector.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_device_detail.*
import no.bakkenbaeck.porchpirateprotector.R
import no.bakkenbaeck.porchpirateprotector.extension.showAndStartAnimating
import no.bakkenbaeck.porchpirateprotector.extension.stopAnimatingAndHide
import no.bakkenbaeck.porchpirateprotector.manager.KeyStoreManager
import no.bakkenbaeck.pppshared.model.PairedDevice
import no.bakkenbaeck.pppshared.presenter.DeviceDetailPresenter
import no.bakkenbaeck.pppshared.view.DeviceDetailView
import java.lang.RuntimeException

class DeviceDetailFragment: Fragment(), DeviceDetailView {

    private val presenter by lazy { DeviceDetailPresenter(this, currentDevice, KeyStoreManager) }

    private lateinit var currentDevice: PairedDevice

    companion object {
        const val ARG_DEVICE = "device_json"
    }

    // FRAGMENT LIFECYCLE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_device_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deviceJSON = arguments?.getString(ARG_DEVICE)

        deviceJSON?.let {
            val device = PairedDevice.fromJSONString(it)
            device?.let {
                currentDevice = it
            } ?: throw RuntimeException("JSON not parsable to a device!")
        } ?: throw RuntimeException("JSON not found!")

        presenter.getStatus()
        button_lock.setOnClickListener { presenter.lock() }
        button_unlock.setOnClickListener { presenter.unlock() }
    }

    // DEVICE DETAIL VIEW

    override fun setTitle(toString: String) {
        textview_device_detail_name.text = toString
    }

    override fun setLockButtonEnabled(enabled: Boolean) {
        button_lock.isEnabled = enabled
    }

    override fun setUnlockButtonEnabled(enabled: Boolean) {
        button_unlock.isEnabled = enabled
    }

    override fun setApiError(toString: String?) {
        textview_error_device_detail.text = toString
    }

    override fun startLoadingIndicator() {
        progress_bar_device_detail.showAndStartAnimating()
    }

    override fun stopLoadingIndicator() {
        progress_bar_device_detail.stopAnimatingAndHide()
    }
}