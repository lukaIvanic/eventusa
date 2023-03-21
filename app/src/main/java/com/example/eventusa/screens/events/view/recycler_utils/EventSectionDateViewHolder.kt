package com.example.eventusa.screens.events.view.recycler_utils

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventusa.R

class EventSectionDateViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    var textView: TextView;

    init {
        textView = itemView.findViewById(R.id.sectionDateTextView)

    }

}