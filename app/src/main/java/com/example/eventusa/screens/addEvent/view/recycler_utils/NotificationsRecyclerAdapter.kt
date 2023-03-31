package com.example.eventusa.screens.addEvent.view.recycler_utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventusa.R
import com.example.eventusa.screens.addEvent.model.NotificationPreset
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationsRecyclerAdapter(
    private val notificationInfos: MutableList<NotificationPreset> = ArrayList(),
) : RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationViewHolder>() {

    /**
     * Used for notifying activity of a cancel button click on notification row.
     */
    private val _cancelNotifFlow: MutableSharedFlow<Int> =
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
                val wasEmitSuccessful = _cancelNotifFlow.tryEmit(adapterPosition)
                //TODO: try to do something with this emitted value?
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
        notificationInfos[pos].apply {
            holder.notifDescTextView.text = "$notifTimeBeforeEventMins minutes before"
        }

    }


    override fun getItemCount(): Int {
        return notificationInfos.size
    }

    fun emptyList() {
        val size = notificationInfos.size
        notificationInfos.clear()
        notifyItemRangeRemoved(0, size - 1)
    }

    fun updateData(newNotifInfos: List<NotificationPreset>) {
        notificationInfos.clear()
        notificationInfos.addAll(newNotifInfos)
        notifyDataSetChanged()
    }

    fun addNotif(notificationPreset: NotificationPreset) {
        notificationInfos.add(notificationPreset)
        notifyItemInserted(notificationInfos.size - 1)
    }


    fun getNotifs(): List<NotificationPreset> {
        return notificationInfos
    }


    fun getNotif(notifIndex: Int): NotificationPreset {
        return notificationInfos[notifIndex]
    }


}
