package com.example.eventusa.caching.room.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.eventusa.caching.room.extraentities.EventNotification

@Dao
interface NotifsDao {

    @Query("INSERT INTO event_notifications_table (eventId, minutesBeforeEvent, notifId) VALUES(:eventId, :minsBeforeEvent, :notifId)")
    suspend fun insertEventNotif(eventId: Int, minsBeforeEvent: Int, notifId: Int)

    @Query("SELECT * FROM event_notifications_table WHERE eventId= :eventId")
    suspend fun getEventNotifs(eventId: Int): List<EventNotification>

    @Query("SELECT COUNT() FROM event_notifications_table WHERE notifId = :notifId LIMIT 1")
    suspend fun getEventNotifCount(notifId: Int): Int

    suspend fun notifExists(notifId: Int): Boolean{
        val count = getEventNotifCount(notifId)
        return count > 0
    }

    @Update
    suspend fun updateEventNotif(eventNotification: EventNotification)

    @Query("DELETE FROM event_notifications_table WHERE notifId = :notifId")
    suspend fun deleteEventNotif(notifId: Int)

}