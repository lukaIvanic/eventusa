package com.example.eventusa.caching.room

import com.example.eventusa.caching.room.extraentities.EventNotification
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.screens.login.model.room.RoomUser
import kotlinx.coroutines.delay

// Room helper class
object Room {

    private val eventsDao by lazy { EventusaDatabase.getInstance().getEventsDao() }
    private val usersDao by lazy { EventusaDatabase.getInstance().getUsersDao() }
    private val notifsDao by lazy { EventusaDatabase.getInstance().getEventNotificationsDao() }

    /**
     * Events sections
     * All of CRUD available.
     */
    suspend fun insertEvent(rinetEvent: RINetEvent) {
        eventsDao.insert(rinetEvent.copy(eventId = 0))
    }

    suspend fun readAllEvents(): List<RINetEvent> {
        delay(500)
        return eventsDao.getAllEvents()
    }

    suspend fun updateEvent(rinetEvent: RINetEvent) {
        eventsDao.updateEvent(rinetEvent)
    }

    suspend fun deleteEvent(eventId: Int) {
        return eventsDao.deleteEvent(eventId)
    }


    /**
     * Users section.
     * Db is populated manually.
     * Users can't be created, only read.
     * Returns list.
     */
    suspend fun findUserInDb(roomUser: RoomUser): RoomUser? {
        return usersDao.getRoomUser(roomUser.username, roomUser.password)
    }


    /**
     * Event notifications section.
     */
    suspend fun insertEventNotification(eventNotification: EventNotification) {

        if(notifsDao.notifExists(eventNotification.notifId)) return

        notifsDao.insertEventNotif(
            eventNotification.eventId,
            eventNotification.minutesBeforeEvent,
            eventNotification.notifId
        )
    }

    suspend fun getEventNotifications(eventId: Int): List<EventNotification> {
        return notifsDao.getEventNotifs(eventId)
    }

    suspend fun updateEventNotification(eventNotification: EventNotification) {
        return notifsDao.updateEventNotif(eventNotification)
    }

    suspend fun deleteEventNotification(notifId: Int) {
        return notifsDao.deleteEventNotif(notifId)
    }




}