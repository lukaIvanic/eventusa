package com.example.eventusa.screens.events.data

import com.example.eventusa.screens.events.data.EventItemType.EVENT_SECTION_DATE

//TODO: Make this date from and date to and transfer functionality of converting them to display string to here
data class EventSectionHeader(val text: String) : EventItem {
    override val type: EventItemType
        get() = EVENT_SECTION_DATE
}
