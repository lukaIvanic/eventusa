package com.example.eventusa.utils.notifications

import org.junit.Test

class NotifManagerTest {


    @Test
    fun generateNotificationId_maxPositiveValues_noException() {
        val eventId = Int.MAX_VALUE
        val minsUntilEvent = Int.MAX_VALUE
        NotifManager.generateNotificationId(eventId, minsUntilEvent)
        assert(true)
    }


    @Test
    fun generateNotificationId_maxMinsValues_noException() {
        val eventId = 1231312 // some very large number
        val minsUntilEvent = MAX_MINS_UNTIL_EVENT
        NotifManager.generateNotificationId(eventId, minsUntilEvent)
        assert(true)
    }

    @Test
    fun generateNotificationId_negativeEventId_noException() {
        val eventId = -23215 // some very large number
        val minsUntilEvent = 60
        NotifManager.generateNotificationId(eventId, minsUntilEvent)
        assert(true)
    }

}