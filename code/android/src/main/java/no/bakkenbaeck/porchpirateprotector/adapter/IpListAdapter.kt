package no.bakkenbaeck.porchpirateprotector.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.bakkenbaeck.porchpirateprotector.R

interface IpSelectionListener {
    fun selectedIpAddress(ipAddress: String)
}

class IpHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val ipTextView: TextView = itemView.findViewById(R.id.textview_ip_address)
}

class IpListAdapter(private val listener: IpSelectionListener): RecyclerView.Adapter<IpHolder>() {
    var ipAddresses: List<String> = listOf()
        set(value) {
            field = value
            this@IpListAdapter.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IpHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_ip_address, parent, false)
        return IpHolder(view)
    }

    override fun getItemCount(): Int {
        return ipAddresses.count()
    }

    override fun onBindViewHolder(holder: IpHolder, position: Int) {
        val address = ipAddresses.get(position)
        holder.ipTextView.text = address

        holder.itemView.setOnClickListener { listener.selectedIpAddress(address) }
    }
}