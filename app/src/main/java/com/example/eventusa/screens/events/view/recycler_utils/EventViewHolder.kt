package com.example.eventusa.screens.events.view.recycler_utils

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.eventusa.R

class EventViewHolder(itemView: View, val onEventClick: (eventPos: Int) -> Unit) : ViewHolder(itemView) {

    var eventTitleTextView: TextView
    var dayInMonthTextView: TextView
    var dayInWeekTextView: TextView
    var eventPeriodTextView: TextView
    var eventLayout: LinearLayout

    init{
        eventTitleTextView = itemView.findViewById(R.id.eventTitleTextView)
        dayInMonthTextView = itemView.findViewById(R.id.dayInMonthTextView)
        dayInWeekTextView = itemView.findViewById(R.id.dayInWeekTextView)
        eventPeriodTextView = itemView.findViewById(R.id.eventPeriodTextView)
        eventLayout = itemView.findViewById(R.id.eventLayout)

        itemView.setOnClickListener {
            onEventClick(adapterPosition)
        }
    }

}