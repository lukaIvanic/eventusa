package com.example.eventusa.screens.events.data

import com.example.eventusa.extensions.PATTERN_SERVER
import com.example.eventusa.extensions.toParsedString
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime


data class RINetEvent(
    val eventId: Int? = 0,
    @JsonProperty("Name")
    var title: String?,
    @JsonProperty("From")
    var startDateTime: LocalDateTime,
    @JsonProperty("To")
    var endDateTime: LocalDateTime,
    var location: String? = "",
    var description: String? = "",
    val calendar: Boolean? = false,
    val days: Int? = 0,
    val dateAddded: String? = LocalDateTime.now().toParsedString(PATTERN_SERVER),
) : EventItem {

    override val type: EventItemType
        get() = EventItemType.EVENT

}

