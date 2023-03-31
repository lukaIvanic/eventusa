package com.example.eventusa.utils.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast

const val MAX_MINS_UNTIL_EVENT = 1000000 // 20 mjeseci

class NotifManager(
    val context: Context,
) {


    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleExactAlarm(
        eventId: Int,
        title: String,
        desc: String,
        eventTimeEpochSeconds: Long,
        minsUntilEvent: Int,
    ) {

        if (canScheduleExactAlarms().not()) {
            //TODO rewrite
            Toast.makeText(
                context,
                "Don't have notification permissions, please update settings.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val notificationId = generateNotificationId(eventId, minsUntilEvent)
        // 1
        val pendingIntent =
            createExactAlarmIntent(notificationId, title, desc, eventId, minsUntilEvent)
        // 2
        val alarmClockInfo =
            AlarmManager.AlarmClockInfo(eventTimeEpochSeconds * 1000L, pendingIntent)
        // 3
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    fun deleteExactAlarm(eventId: Int, minsUntilEvent: Int) {
        val notifId = generateNotificationId(eventId, minsUntilEvent)
        val pendingIntent = createExactAlarmIntent(notifId)
        alarmManager.cancel(pendingIntent)
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }


    private fun createExactAlarmIntent(
        notificationId: Int,
        title: String = "",
        desc: String = "",
        eventId: Int = -1,
        minsUntilEvent: Int = -1,
    ): PendingIntent {

        // 1
        val intent = Intent(context, ExactAlarmBroadcastReceiver::class.java)
        intent.putExtra("notification_id", notificationId)
        intent.putExtra("notification_title", title)
        intent.putExtra("notification_desc", desc)
        intent.putExtra("event_id", eventId)
        intent.putExtra("mins_until_event", minsUntilEvent)


        // 2
        return PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    }


    companion object {

        // eventId max -> 999
        // minsUntilEvent max -> MAX_MINS_UNTIL_EVENT = 999999 / 20 months
        fun generateNotificationId(eventId: Int, minsUntilEvent: Int): Int {
            val safeEventId = if (eventId < 0) -eventId else eventId

            return "${safeEventId % 1000}${minsUntilEvent % MAX_MINS_UNTIL_EVENT}".toInt()
        }

    }


}

