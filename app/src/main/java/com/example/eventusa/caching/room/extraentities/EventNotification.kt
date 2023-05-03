package com.example.eventusa.caching.room.extraentities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.random.Random

/**
 * Class representing a reminder notification for an event.
 * @param minutesBeforeEvent represent at what time before the start of the event should the reminder notification trigger.
 * @see com.example.eventusa.caching.room.daos.NotifsDao
 */
@Entity("event_notifications_table")
data class EventNotification(
    var eventId: Int,
    var minutesBeforeEvent: Int,
) {

    @PrimaryKey()
    var notifId: Int = Random.nextInt(0, Integer.MAX_VALUE - 1)

}