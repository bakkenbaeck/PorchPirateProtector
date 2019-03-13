package no.bakkenbaeck.porchpirateprotector.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.bakkenbaeck.porchpirateprotector.R
import no.bakkenbaeck.pppshared.model.PairedDevice

interface DeviceSelectionListener {
    fun deviceSelected(device: PairedDevice)
}

class DeviceHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val nameTextView: TextView = itemView.findViewById(R.id.textview_device_id)
    val lockedTextView: TextView = itemView.findViewById(R.id.textview_device_lock_state)
}

class DeviceListAdapter(private val listener: DeviceSelectionListener): RecyclerView.Adapter<DeviceHolder>() {
    var list: List<PairedDevice> = listOf()
        set(value) {
            field = value
            this@DeviceListAdapter.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_paired_device, parent, false)
        return DeviceHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
        val item = list.get(position)
        holder.nameTextView.text = "Device #${item.deviceId}"

        when (item.lockState?.isLocked) {
            true -> holder.lockedTextView.text = "🔐"
            false -> holder.lockedTextView.text = "🔓"
            null -> holder.lockedTextView.text = "❓"
        }

        holder.itemView.setOnClickListener { listener.deviceSelected(item) }
    }
}