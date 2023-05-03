package com.example.eventusa.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.eventusa.caching.room.Room
import com.example.eventusa.caching.room.extraentities.EventNotification
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.utils.extensions.getPeriod
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.random.Random

class NotifManager(
    val context: Context,
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    suspend fun createOrUpdateEventNotif(
        event: RINetEvent,
        minsUntilEvent: Int,
        existingNotifId: Int? = null,
    ): Boolean {

        val eventNotification = EventNotification(event.eventId, minsUntilEvent)
        if (existingNotifId != null) {
            eventNotification.notifId = existingNotifId
        }else{
            eventNotification.notifId = Random.nextInt(0, Integer.MAX_VALUE)
        }

        if (existingNotifId == null) {
            Room.insertEventNotification(eventNotification)
        }

        return scheduleExactAlarm(
            notifId = eventNotification.notifId,
            title = event.title ?: "Event",
            desc = event.getPeriod(),
            notifTimeEpochSeconds = calculateNotifEpochTime(event.startDateTime, minsUntilEvent),
            eventId = event.eventId
        )

    }

    private fun scheduleExactAlarm(
        notifId: Int,
        title: String,
        desc: String,
        notifTimeEpochSeconds: Long,
        eventId: Int,
    ): Boolean {

        if (canScheduleExactAlarms().not()) {
            return false
        }


        val pendingIntent =
            createExactAlarmIntent(notifId, title, desc, notifTimeEpochSeconds, eventId)
        val alarmClockInfo =
            AlarmManager.AlarmClockInfo(notifTimeEpochSeconds * 1000L, pendingIntent)
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

        return true
    }


    suspend fun deleteEventNotification(notifId: Int) {

        Room.deleteEventNotification(notifId)
        deleteExactAlarm(notifId)

    }

    private fun deleteExactAlarm(notifId: Int) {
        val pendingIntent = createExactAlarmIntent(notifId, "", "", 0, -1)
        alarmManager.cancel(pendingIntent)
    }

    private fun createExactAlarmIntent(
        notifId: Int,
        title: String,
        desc: String,
        notifTimeEpochSeconds: Long,
        eventId: Int,
    ): PendingIntent {


        val intent = Intent(context, ExactAlarmBroadcastReceiver::class.java)
        intent.putExtra("notification_id", notifId)
        intent.putExtra("notification_title", title)
        intent.putExtra("notification_desc", desc)
        intent.putExtra("event_id", eventId)


        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT


        return PendingIntent.getBroadcast(
            context,
            notifId,
            intent,
            flags
        )


    }

    private fun calculateNotifEpochTime(startDateTime: LocalDateTime, minsUntilEvent: Int): Long {
        val triggerTime = startDateTime
            .minusMinutes(minsUntilEvent.toLong())
            .atZone(ZoneId.systemDefault())
            .toEpochSecond()

        val eventDateTime = startDateTime
        val triggerDateTime = LocalDateTime.ofEpochSecond(triggerTime, 0, ZoneOffset.ofHours(0))

        Log.i("LUKA", "event: $eventDateTime, reminder trigger: $triggerDateTime")
        return triggerTime
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

}

