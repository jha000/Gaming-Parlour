package com.app.gamingparlour.adapter

import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.app.gamingparlour.R
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.gamingparlour.data.Monitor

class MonitorAdapter(private val monitors: List<Monitor>, private val listener: MonitorSelectionListener) :
    RecyclerView.Adapter<MonitorAdapter.ViewHolder>() {

    private var selectedItemPosition = 0
    private var selectedMonitorName = monitors[0].name

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monitor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMonitor = monitors[position]
        holder.bind(currentMonitor, position == selectedItemPosition)
    }

    override fun getItemCount(): Int {
        return monitors.size
    }

    fun getSelectedMonitorName(): String {
        return selectedMonitorName!!
    }

    interface MonitorSelectionListener {
        fun onMonitorSelected(name: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val monitorNameTextView: TextView = itemView.findViewById(R.id.monitorNameTextView)
        private val slots: TextView = itemView.findViewById(R.id.totalslots)
        private val layout: LinearLayout = itemView.findViewById(R.id.layout)

        init {
            itemView.setOnClickListener {
                val previousSelectedItem = selectedItemPosition
                selectedItemPosition = adapterPosition
                selectedMonitorName = monitors[adapterPosition].name
                notifyItemChanged(previousSelectedItem)
                notifyItemChanged(selectedItemPosition)

                listener.onMonitorSelected(selectedMonitorName!!)
            }
        }

        fun bind(monitor: Monitor, isSelected: Boolean) {
            monitorNameTextView.text = monitor.name
            slots.text = monitor.totalSlots.toString()
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.monitorbac)
                layout.visibility=View.GONE
                monitorNameTextView.setTextColor(itemView.context.getColor(android.R.color.black))
            } else {
                itemView.setBackgroundResource(R.drawable.whitebac)
                monitorNameTextView.setTextColor(itemView.context.getColor(android.R.color.white))
            }
        }
    }
}
