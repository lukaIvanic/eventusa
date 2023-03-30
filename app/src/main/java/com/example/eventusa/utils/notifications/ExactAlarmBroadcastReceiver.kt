package com.example.eventusa.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.eventusa.R
import com.example.eventusa.screens.addEvent.view.AddEventActivity

private const val NOTIFICATION_CHANNEL_ID = "event_alarms"
private const val NOTIFICATION_CHANNEL_NAME = "Event Alarms"

class ExactAlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", -1)
        val title = intent.getStringExtra("notification_title") ?: return
        val desc = intent.getStringExtra("notification_desc") ?: ""

        // 2
        showNotification(
            context,
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            notificationId,
            title,
            desc
        )
    }

    fun showNotification(
        context: Context,
        channelId: String,
        channelName: String,
        notificationId: Int,
        contentTitle: String,
        contentDesc: String
    ) {
        val startAppIntent = Intent(context, AddEventActivity::class.java)
        val startAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            startAppIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val deleteIntent = Intent(context, AlarmNotificationDismissedBroadcastReceiver::class.java)
        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            deleteIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && notificationManager.getNotificationChannel(channelId) == null
        ) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.settings_icon)
            .setContentTitle(contentTitle)
            .setContentText(contentDesc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setFullScreenIntent(startAppPendingIntent, true)
            .setDeleteIntent(deletePendingIntent)

        val notification = notificationBuilder.build()

        notificationManager.notify(notificationId, notification)

    }


}

