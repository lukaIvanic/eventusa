package com.example.eventusa.screens.events.data

enum class EventItemType {
    EVENT_SECTION_DATE,
    EVENT
}

interface EventItem {
    val type: EventItemType
}