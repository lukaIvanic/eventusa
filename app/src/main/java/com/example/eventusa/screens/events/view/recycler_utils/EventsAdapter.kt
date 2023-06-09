package com.example.eventusa.screens.events.view.recycler_utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.eventusa.R
import com.example.eventusa.screens.events.data.*
import com.example.eventusa.utils.extensions.*
import java.time.format.DateTimeFormatter

class EventsAdapter(
    private var eventItemList: MutableList<EventItem> = ArrayList(),
    val onEventClick: (riNetEvent: RINetEvent) -> Unit,
    val onScrollNeeded: (itemIndex: Int) -> Unit
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
                val topMargin = if (eventItemList[position - 1] is EventSectionHeader) 20F else 4F
                eventViewHolder.itemView.setCustomMargins(top = topMargin)

                eventViewHolder.dayInMonthTextView.setBackgroundResource(R.drawable.app_background_rounded)
                eventViewHolder.dayInMonthTextView.setTextColor(Color.parseColor("#252525"))
                eventViewHolder.dayInWeekTextView.setTextColor(Color.parseColor("#404040"))

                eventViewHolder.dayInMonthTextView.text = ""
                eventViewHolder.dayInWeekTextView.text = ""

                eventViewHolder.itemView.setCustomMargins(top = 4F)

                // Remove date on left side of event if the date is already displayed in above events
                if (rinetEvent.isFirstInDate) {
                    eventViewHolder.dayInMonthTextView.text =
                        rinetEvent.startDateTime.dayOfMonth.toString()
                    eventViewHolder.dayInWeekTextView.text =
                        rinetEvent.startDateTime.format(DateTimeFormatter.ofPattern("EEE"))

                    eventViewHolder.itemView.setCustomMargins(top = 20F)

                    if (rinetEvent.startDateTime.isToday()) {
                        eventViewHolder.dayInMonthTextView.setBackgroundResource(R.drawable.secondary_color_rounded)
                        eventViewHolder.dayInMonthTextView.setTextColor(Color.parseColor("#FFFBF8"))
                        eventViewHolder.dayInWeekTextView.setTextColor(Color.parseColor("#9B2F0C"))
                    }

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
                    EventColors.getColorId(
                        rinetEvent.eventColor
                    )
                )


            }


        }


    }

    override fun getItemViewType(position: Int): Int {
        return eventItemList.get(position).type.ordinal
    }

    override fun getItemCount(): Int {
        return eventItemList.size
    }

    fun updateEvents(eventItems: MutableList<EventItem>) {

        val diffCallback = EventsDiffCallBack(this.eventItemList, eventItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.eventItemList.clear()
        this.eventItemList.addAll(eventItems)
        diffResult.dispatchUpdatesTo(object: ListUpdateCallback{
            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
                onScrollNeeded(position)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
                onScrollNeeded(toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                notifyItemRangeChanged(position, count, payload)
                onScrollNeeded(position)
            }
        })

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
            return oldItem == newItem
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
            return oldItem == newItem
        }
        return false
    }

}
