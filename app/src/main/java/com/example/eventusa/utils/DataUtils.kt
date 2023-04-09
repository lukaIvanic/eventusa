package com.example.eventusa.utils

import com.example.eventusa.utils.extensions.PATTERN_UI_DATE_SHORT
import com.example.eventusa.utils.extensions.toParsedString
import com.example.eventusa.screens.events.data.EventItem
import com.example.eventusa.screens.events.data.EventSectionHeader
import com.example.eventusa.screens.events.data.RINetEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DataUtils {


    fun eventsDisplayItems(_rawEvents: MutableList<RINetEvent>): MutableList<EventItem> {

        val rawEvents: MutableList<RINetEvent> = ArrayList()
        rawEvents.addAll(_rawEvents.sortedWith(object : Comparator<RINetEvent> {
            override fun compare(event1: RINetEvent?, event2: RINetEvent?): Int {
                val start1 = event1?.startDateTime
                val start2 = event2?.startDateTime

                if (start1 != null && start2 != null) {
                    return if (start1 > start2) 1 else 0
                }

                return 0
            }

        }))


        val eventItems: MutableList<EventItem> = ArrayList()

        rawEvents.sortedBy { it.startDateTime }

        val eventsBeforeToday =
            rawEvents.filter { it.startDateTime.toLocalDate() < LocalDate.now() }
        rawEvents.removeAll(eventsBeforeToday)
        if (eventsBeforeToday.isNotEmpty()) {
            var header =
                eventsBeforeToday[0].startDateTime.format(DateTimeFormatter.ofPattern("MMM dd"))
            if (eventsBeforeToday[0].startDateTime.toLocalDate() < LocalDate.now().minusDays(1)) {
                header += " - Yesterday"
            } else {
                header = "Yesterday"
            }

            eventItems.add(
                EventSectionHeader(
                    header
                )
            )
        }
        eventItems.addAll(eventsBeforeToday)

        val eventsToday =
            rawEvents.filter { isSameDay(it.startDateTime.toLocalDate(), LocalDate.now()) }
        rawEvents.removeAll(eventsToday)
        if (eventsToday.isNotEmpty()) {
            eventItems.add(
                EventSectionHeader(
                    "Today, " + LocalDate.now().toParsedString(PATTERN_UI_DATE_SHORT)
                )

            )
        }
        eventItems.addAll(eventsToday)

        val eventsTomorrowToEndOfWeek = rawEvents.filter {
            it.startDateTime.toLocalDate() >= LocalDate.now()
                .plusDays(1) && it.startDateTime.toLocalDate() < LocalDate.now().plusWeeks(1)
                .with(DayOfWeek.MONDAY)
        }
        rawEvents.removeAll(eventsTomorrowToEndOfWeek)
        if (eventsTomorrowToEndOfWeek.isNotEmpty()) {
            eventItems.add(
                EventSectionHeader(
                    "Tomorrow - " + LocalDate.now().with(DayOfWeek.SUNDAY).format(
                        DateTimeFormatter.ofPattern("dd MMM")
                    )
                )
            )
        }
        eventItems.addAll(eventsTomorrowToEndOfWeek)


        var currentWeeksItems: MutableList<EventItem> = ArrayList()
        var currWeekMonDate = LocalDate.now().with(DayOfWeek.MONDAY)

        rawEvents.forEach { riNetEvent ->

            // Check for new week
            if (riNetEvent.startDateTime.toLocalDate().with(DayOfWeek.MONDAY) != currWeekMonDate) {
                currWeekMonDate = riNetEvent.startDateTime.toLocalDate().with(DayOfWeek.MONDAY)


                // Confirm a week section
                if (currentWeeksItems.isNotEmpty()) {
                    if (riNetEvent.startDateTime.toLocalDate() > LocalDate.now()) {
                        eventItems.addAll(currentWeeksItems)
                    } else {
                        eventItems.addAll(0, currentWeeksItems)
                    }
                    currentWeeksItems.clear()
                }
            }

            // Add Events section title
            if (currentWeeksItems.isEmpty()) {
                currentWeeksItems.add(generateEventsSectionHeader(currWeekMonDate))
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