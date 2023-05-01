package com.example.eventusa.caching.room.daos

import androidx.room.*
import com.example.eventusa.screens.events.data.RINetEvent

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rinetEvent: RINetEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rinetEvent: List<RINetEvent>)

    @Query("SELECT * FROM rinetevents_table ORDER BY startDateTime")
    suspend fun getAllEvents(): List<RINetEvent>

    @Update
    suspend fun updateEvent(rinetEvent: RINetEvent)

    @Query("DELETE FROM rinetevents_table WHERE eventId= :eventId")
    suspend fun deleteEvent(eventId: Int)

}