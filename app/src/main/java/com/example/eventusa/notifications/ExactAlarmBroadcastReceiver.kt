package com.example.eventusa.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.eventusa.R
import com.example.eventusa.caching.room.EventusaDatabase
import com.example.eventusa.caching.room.Room
import com.example.eventusa.screens.addEvent.view.activity.AddEventActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val REMINDER_NOTIFICATION_CHANNEL_ID = "event_reminder_alarms"
private const val REMINDER_NOTIFICATION_CHANNEL_NAME = "Reminder alarms"

class ExactAlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", -1)
        val title = intent.getStringExtra("notification_title")
        val desc = intent.getStringExtra("notification_desc")

        val eventId = intent.getIntExtra(
            "event_id",
            -1
        ) // Used for navigating to event details screen on notif click


        if (title == null || desc == null) {

            if (notificationId != -1) {
                removeNotificationFromRoom(context, notificationId)
            }
            return
        }


        showNotification(
            context,
            notificationId,
            title,
            desc,
            eventId
        )
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        contentTitle: String,
        contentDesc: String,
        eventId: Int,
    ) {

        removeNotificationFromRoom(context, notificationId)

        val startAppIntent = Intent(context, AddEventActivity::class.java)
        startAppIntent.putExtra("event_id", eventId)
        startAppIntent.putExtra("is_from_notif", true)
        startAppIntent.action = Random.nextInt().toString()
        val startAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            startAppIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val deleteIntent = Intent(context, AlarmNotificationDismissedBroadcastReceiver::class.java)
        deleteIntent.putExtra("notif_id", notificationId)
        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            deleteIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val notificationManager = context.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && notificationManager.getNotificationChannel(REMINDER_NOTIFICATION_CHANNEL_ID) == null
        ) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    REMINDER_NOTIFICATION_CHANNEL_ID,
                    REMINDER_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        val notificationBuilder =
            NotificationCompat.Builder(context, REMINDER_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.settings_icon)
                .setContentTitle(contentTitle)
                .setContentText(contentDesc)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(startAppPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .setOngoing(false)
                .setAutoCancel(true)

        val notification = notificationBuilder.build()



        notificationManager.notify(notificationId, notification)


    }

    private fun removeNotificationFromRoom(context: Context, notifId: Int) {
        EventusaDatabase.setupInstance(context.applicationContext)

        CoroutineScope(NonCancellable).launch {
            Room.deleteEventNotification(notifId)
        }

    }


}

