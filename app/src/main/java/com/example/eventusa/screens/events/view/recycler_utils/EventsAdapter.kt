package com.example.eventusa.screens.events.view.recycler_utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.eventusa.R
import com.example.eventusa.screens.events.data.*
import com.example.eventusa.utils.extensions.*
import java.time.format.DateTimeFormatter

class EventsAdapter(
    private var eventItemList: MutableList<EventItem> = ArrayList(),
    val onEventClick: (riNetEvent: RINetEvent) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        if (viewType == EventItemType.EVENT.ordinal) {
            return EventViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_event, parent, false)
            ) { eventIndex ->
                onEventClick(eventItemList[eventIndex] as RINetEvent)
            }
        }


        return EventSectionDateViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_event_section_date, parent, false)
        )

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (eventItemList.get(position)) {

            is EventSectionHeader -> {
                val sectionDateViewHolder = holder as EventSectionDateViewHolder
                val eventSectionHeader = eventItemList[holder.adapterPosition] as EventSectionHeader

                sectionDateViewHolder.textView.text = eventSectionHeader.text

            }

            is RINetEvent -> {
                val eventViewHolder = holder as EventViewHolder
                val rinetEvent = eventItemList[holder.adapterPosition] as RINetEvent

                eventViewHolder.eventTitleTextView.text = rinetEvent.title

                // Check if event is first in a section for margin correction
                val topMargin = if (eventItemList[position - 1] is EventSectionHeader) 16F else 8F
                eventViewHolder.itemView.setCustomMargins(top = topMargin)

                // Remove date on left side of event if the date is already displayed in above events
                if (isEventFirstInDate(position, rinetEvent)) {
                    eventViewHolder.dayInMonthTextView.text =
                        rinetEvent.startDateTime.dayOfMonth.toString()
                    eventViewHolder.dayInWeekTextView.text =
                        rinetEvent.startDateTime.format(DateTimeFormatter.ofPattern("EEE"))

                    eventViewHolder.itemView.setCustomMargins(top = 20F)

                    if (rinetEvent.startDateTime.isToday()) {
                        eventViewHolder.dayInMonthTextView.setBackgroundResource(R.drawable.secondary_color_roundede)
                        eventViewHolder.dayInMonthTextView.setTextColor(Color.parseColor("#FFFBF8"))
                        eventViewHolder.dayInWeekTextView.setTextColor(Color.parseColor("#9B2F0C"))
                    } else {
                        eventViewHolder.dayInMonthTextView.setBackgroundResource(R.drawable.app_background_rounded)
                        eventViewHolder.dayInMonthTextView.setTextColor(Color.parseColor("#252525"))
                        eventViewHolder.dayInWeekTextView.setTextColor(Color.parseColor("#404040"))
                    }

                } else {
                    eventViewHolder.dayInMonthTextView.text = ""
                    eventViewHolder.dayInWeekTextView.text = ""

                    eventViewHolder.itemView.setCustomMargins(top = 8F)


                }

                eventViewHolder.eventPeriodTextView.visibility = View.GONE

                if (rinetEvent.isFirstInSeries) {
                    eventViewHolder.eventPeriodTextView.visibility = View.VISIBLE
                    eventViewHolder.eventPeriodTextView.text = rinetEvent.getPeriodFirstInSeries()
                } else if (rinetEvent.isLastInSeries) {
                    eventViewHolder.eventPeriodTextView.visibility = View.VISIBLE
                    eventViewHolder.eventPeriodTextView.text = rinetEvent.getPeriodLastInSeries()
                } else if (rinetEvent.isInSeries) {
                    eventViewHolder.eventPeriodTextView.visibility = View.GONE
                } else {
                    eventViewHolder.eventPeriodTextView.visibility = View.VISIBLE
                    eventViewHolder.eventPeriodTextView.text =
                        rinetEvent.getPeriod()
                }

                // Set event color
                eventViewHolder.eventLayout.setBackgroundResource(
                    EventColors.getDrawableId(
                        rinetEvent.eventColor
                    )
                )


            }


        }


    }

    private fun isEventFirstInDate(
        position: Int,
        rinetEvent: RINetEvent,
    ) =
        position > 0 && (eventItemList[position - 1] as? RINetEvent)?.startDateTime?.dayOfWeek != rinetEvent.startDateTime.dayOfWeek

    override fun getItemViewType(position: Int): Int {
        return eventItemList.get(position).type.ordinal
    }

    override fun getItemCount(): Int {
        return eventItemList.size
    }

    fun updateEvents(eventItems: MutableList<EventItem>) {

        eventItemList = eventItems
        notifyDataSetChanged()
        return

        val diffCallback = EventsDiffCallBack(this.eventItemList, eventItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.eventItemList.clear()
        this.eventItemList.addAll(eventItems)
        diffResult.dispatchUpdatesTo(this@EventsAdapter)

    }

}

private class EventsDiffCallBack(
    private var oldEvents: MutableList<EventItem>,
    private var newEvents: MutableList<EventItem>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldEvents.size
    override fun getNewListSize(): Int = newEvents.size


    override fun areItemsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {

        val oldItem = oldEvents[oldItemPos]
        val newItem = newEvents[newItemPos]

        if (oldItem.type != newItem.type) {
            return false
        }

        if (oldItem is EventSectionHeader && newItem is EventSectionHeader) {
            return oldItem.text == newItem.text
        }

        if (oldItem is RINetEvent && newItem is RINetEvent) {
            return (oldItem.eventId == newItem.eventId && oldItem.title == newItem.title && oldItem.startDateTime == newItem.startDateTime && oldItem.endDateTime == newItem.endDateTime)
        }
        return false
    }


    override fun areContentsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {

        val oldItem = oldEvents[oldItemPos]
        val newItem = newEvents[newItemPos]

        if (oldItem.type != newItem.type) {
            return false
        }

        if (oldItem is EventSectionHeader && newItem is EventSectionHeader) {
            return oldItem.text == newItem.text
        }

        if (oldItem is RINetEvent && newItem is RINetEvent) {
            return (oldItem.eventId == newItem.eventId && oldItem.title == newItem.title && oldItem.startDateTime == newItem.startDateTime && oldItem.endDateTime == newItem.endDateTime)
        }
        return false
    }

}
