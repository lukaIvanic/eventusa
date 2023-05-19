package com.example.eventusa.utils

import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.screens.events.data.EventItem
import com.example.eventusa.screens.events.data.EventSectionHeader
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.utils.extensions.getDaysLasting
import com.example.eventusa.utils.extensions.isSameDate
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DataUtils {

    /**
     * Takes a list of sorted rinetEvents and returns a list with explicit sections of events.
     *
     * Example => Input: ev1, ev2, ev3, ev4, ev5, ev6, ev7 -> Output: section1, ev1, ev2, ev3, section2, ev4, section5, ev6, ev7
     */
    fun eventsDisplayItems(_rawEvents: MutableList<RINetEvent>): MutableList<EventItem> {

        val rawEventsUnsorted: MutableList<RINetEvent> = ArrayList()


        // Separate multiple days event to multiple events
        _rawEvents.forEachIndexed { indx, _rinetevent ->

            if (_rinetevent.getDaysLasting() == 1) {
                rawEventsUnsorted.add(_rinetevent)
                return@forEachIndexed
            }

            var rinetevent = _rinetevent.copy(isInSeries = true)


            for (i in 0 until rinetevent.getDaysLasting()) {

                if (LocalStorageManager.readShowMultipleDayEvents()
                        .not() && (i != rinetevent.getDaysLasting() - 1) && i != 0
                ) {
                    continue
                }

                val newStartDateTime = rinetevent.startDateTime.plusDays((i).toLong())
                val newEndDateTime = newStartDateTime.withHour(rinetevent.endDateTime.hour)
                    .withMinute(rinetevent.endDateTime.minute)

                rawEventsUnsorted.add(
                    rinetevent.copy(
                        startDateTime = newStartDateTime,
                        endDateTime = newEndDateTime,
                        title = rinetevent.title + " (${i + 1}/${rinetevent.getDaysLasting()})",
                        isInSeries = true,
                        isFirstInSeries = i == 0,
                        isLastInSeries = i == rinetevent.getDaysLasting() - 1
                    )
                )
            }

        }

        val rawEvents: MutableList<RINetEvent> = ArrayList()

        rawEvents.addAll(rawEventsUnsorted.sortedBy { it.startDateTime })


        val eventItems: MutableList<EventItem> = ArrayList()



        var currentWeeksItems: MutableList<EventItem> = ArrayList()
        var currWeekMonDate = LocalDate.now().with(DayOfWeek.MONDAY)

        for (i in 0 until rawEvents.size) {

            val riNetEvent = rawEvents.get(i)

            // Check for new week
            if (riNetEvent.startDateTime.toLocalDate().with(DayOfWeek.MONDAY) != currWeekMonDate) {
                currWeekMonDate = riNetEvent.startDateTime.toLocalDate().with(DayOfWeek.MONDAY)


                // Confirm a week section
                if (currentWeeksItems.isNotEmpty()) {
                    eventItems.addAll(currentWeeksItems)
                    currentWeeksItems.clear()
                }
            }

            // Add Events section title
            if (currentWeeksItems.isEmpty()) {
                currentWeeksItems.add(generateEventsSectionHeader(currWeekMonDate))
            }

            if(i >= 1 && riNetEvent.startDateTime.isSameDate(rawEvents.get(i-1).startDateTime)){
                riNetEvent.isFirstInDate = false
            }else{
                riNetEvent.isFirstInDate = true
            }

            currentWeeksItems.add(riNetEvent)


        }

        eventItems.addAll(currentWeeksItems)


        return eventItems
    }

    private fun isSameDay(localDate: LocalDate, localDate2: LocalDate): Boolean {
        return localDate.isEqual(localDate2)
    }

    private fun generateEventsSectionHeader(currWeekMonDate: LocalDate): EventSectionHeader {
        var startPattern =
            if (currWeekMonDate.month == currWeekMonDate.with(DayOfWeek.SUNDAY).month) "dd" else "dd MMM"

        var endPattern = "dd MMM"

        if (currWeekMonDate.year != LocalDate.now().year) {
            startPattern = "MMM dd, YYYY"
            endPattern = "MMM dd, YYYY"
        }

        val weekStart = currWeekMonDate.format(DateTimeFormatter.ofPattern(startPattern))
        val weekEnd = currWeekMonDate.with(DayOfWeek.SUNDAY)
            .format(DateTimeFormatter.ofPattern(endPattern))
        return EventSectionHeader("$weekStart - $weekEnd")
    }
}