package com.example.eventusa.screens.events.data

enum class EventItemType {
    EVENT_SECTION_DATE,
    EVENT
}

/**
 * Empty interface for events recyclerview where 2 row types are needed.
 * @see EventSectionHeader
 * @see RINetEvent
 * @see com.example.eventusa.screens.events.view.recycler_utils.EventsAdapter
 */
interface EventItem {
    val type: EventItemType
}