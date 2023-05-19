package com.example.eventusa.screens.addEvent.view.recycler_utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventusa.R
import com.example.eventusa.caching.room.extraentities.EventNotification
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


sealed class NotificationsAdapterEvents {

    class LOAD_EVENTS(val eventNotificationsList: List<EventNotification>) :
        NotificationsAdapterEvents()

    class ADD_EVENT(val eventNotification: EventNotification) : NotificationsAdapterEvents()

    class DELETE_EVENT(val eventNotification: EventNotification) : NotificationsAdapterEvents()

}

class NotificationsRecyclerAdapter(
    private var eventNotifications: MutableList<EventNotification> = ArrayList(),
) : RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationViewHolder>() {


    private val _cancelNotifFlow: MutableSharedFlow<EventNotification> =
        MutableSharedFlow(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val cancelNotifFlow = _cancelNotifFlow.asSharedFlow()


    inner class NotificationViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var notifDescTextView: TextView
        var notifCancelButton: ImageView

        init {
            notifDescTextView = itemView.findViewById(R.id.notifRowDescTextView)
            notifCancelButton = itemView.findViewById(R.id.cancelNotifRowButton)

            notifCancelButton.setOnClickListener {
                _cancelNotifFlow.tryEmit(eventNotifications[adapterPosition])
            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {

        return NotificationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_notification, parent, false)
        )

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val pos = position
        eventNotifications[pos].apply {
            holder.notifDescTextView.text = "${this.minutesBeforeEvent} minutes before"
        }

    }


    override fun getItemCount(): Int {
        return eventNotifications.size
    }

    /**
     * Only activates once, on initialization of the activity.
     */
    fun initialLoadEvents(eventNotifications: List<EventNotification>) {
        if (this.eventNotifications.isNotEmpty()) return

        this.eventNotifications.addAll(eventNotifications.sortedBy { it.minutesBeforeEvent })
        notifyItemRangeInserted(0, this.eventNotifications.size)

    }

    fun addNotif(eventNotification: EventNotification) {

        if(eventNotifications.isEmpty()){
            eventNotifications.add(eventNotification)
            notifyItemInserted(0)
            return
        }

        val insertIndex = getInsertPosition(eventNotification)
        eventNotifications.add(insertIndex, eventNotification)
        notifyItemInserted(insertIndex)

    }

    private fun getInsertPosition(newEventNotif: EventNotification): Int {
        eventNotifications.forEachIndexed{ index, eventNotif->

            if(eventNotif.minutesBeforeEvent > newEventNotif.minutesBeforeEvent){
                return index
            }

        }
        return eventNotifications.size
    }

    fun deleteNotif(eventNotification: EventNotification) {
        val index = eventNotifications.indexOf(eventNotification)
        if (index < 0) return
        eventNotifications.removeAt(index)
        notifyItemRemoved(index)


    }

    fun getNotifs(): List<EventNotification>{
        return eventNotifications
    }

}
