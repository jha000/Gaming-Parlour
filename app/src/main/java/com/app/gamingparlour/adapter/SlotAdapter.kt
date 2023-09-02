package com.app.gamingparlour.adapter

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.app.gamingparlour.R
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.gamingparlour.activities.Checkout
import com.app.gamingparlour.data.Slot

class SlotAdapter(private val context: Context, private val slots: List<Slot>) :
    RecyclerView.Adapter<SlotAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slot, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSlot = slots[position]
        holder.startTimeTextView.text = currentSlot.startTime
        holder.endTimeTextView.text = currentSlot.endTime
        holder.statusTextView.text = currentSlot.status
        holder.userIdTextView.text = currentSlot.userId
        holder.passcodeTextView.text = currentSlot.passcode

        holder.statusTextView.text = currentSlot.status

        if (currentSlot.status == "booked") {
            holder.itemView.setBackgroundResource(R.drawable.redbac)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.greenbac)
        }

        // Set an OnClickListener to open the DetailActivity when an item is clicked
        holder.itemView.setOnClickListener {
            if (currentSlot.status == "available") {
                val intent = Intent(context, Checkout::class.java)
                intent.putExtra("startTime", currentSlot.startTime)
                intent.putExtra("endTime", currentSlot.endTime)
                intent.putExtra("status", currentSlot.status)
                intent.putExtra("userId", currentSlot.userId)
                intent.putExtra("passcode", currentSlot.passcode)
                context.startActivity(intent)
            } else {
                // Display a toast message if the status is "booked"
                Toast.makeText(context, "This slot is already booked.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemCount(): Int {
        return slots.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startTimeTextView: TextView = itemView.findViewById(R.id.startTimeTextView)
        val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val userIdTextView: TextView = itemView.findViewById(R.id.userIdTextView)
        val passcodeTextView: TextView = itemView.findViewById(R.id.passcodeTextView)
    }
}
