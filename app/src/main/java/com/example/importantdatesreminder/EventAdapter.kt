package com.example.importantdatesreminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.importantdatesreminder.data.Event
import com.example.importantdatesreminder.databinding.ItemEventBinding

class EventAdapter(
    private val context: Context,
    private val onEventLongClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var events = emptyList<Event>()

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEventLongClick(events[position])
                    return@setOnLongClickListener true
                }
                false
            }
        }

        fun bind(event: Event) {
            binding.tvEventTitle.text = event.title
            binding.tvEventDate.text = event.getFormattedDate()

            val daysUntil = event.getDaysUntil()
            binding.tvDaysLeft.text = when (daysUntil) {
                0 -> context.getString(R.string.today)
                else -> context.getString(R.string.days_left, daysUntil)
            }

            // เปลี่ยนสีตามจำนวนวันที่เหลือ
            val bgColor = when {
                daysUntil == 0 -> context.getColor(R.color.colorRed)
                daysUntil <= 7 -> context.getColor(R.color.colorYellow)
                else -> context.getColor(R.color.colorAccent)
            }
            binding.tvDaysLeft.setBackgroundColor(bgColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size

    fun submitList(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
}