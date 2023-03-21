package com.example.eventusa.screens.events.view.recycler_utils

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.eventusa.R

class EventViewHolder(itemView: View, val onEventClick: (eventPos: Int) -> Unit) : ViewHolder(itemView) {

    var eventTitleTextView: TextView
    var dayInMonthTextView: TextView
    var dayInWeekTextView: TextView
    var eventPeriodTextView: TextView

    init{
        eventTitleTextView = itemView.findViewById(R.id.eventTitleTextView)
        dayInMonthTextView = itemView.findViewById(R.id.dayInMonthTextView)
        dayInWeekTextView = itemView.findViewById(R.id.dayInWeekTextView)
        eventPeriodTextView = itemView.findViewById(R.id.eventPeriodTextView)

        itemView.setOnClickListener {
            onEventClick(adapterPosition)
        }
    }

}