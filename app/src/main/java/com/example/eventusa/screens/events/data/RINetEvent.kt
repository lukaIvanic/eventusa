package com.example.eventusa.screens.events.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eventusa.utils.extensions.PATTERN_SERVER
import com.example.eventusa.utils.extensions.toParsedString
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

@Entity("rinetevents_table")
data class RINetEvent(

    @PrimaryKey(autoGenerate = true)
    var eventId: Int = 0,


    @JsonProperty("Name")
    var title: String?,


    @JsonProperty("From")
    var startDateTime: LocalDateTime = LocalDateTime.now(),


    @JsonProperty("To")
    var endDateTime: LocalDateTime = LocalDateTime.now(),


    var location: String? = "",

    var description: String? = "",

    var calendar: Boolean? = false,

    var days: Int? = 0,

    var dateAddded: String? = LocalDateTime.now().toParsedString(PATTERN_SERVER),

    ) : EventItem {

    constructor() : this(
        0,
        "name",
        LocalDateTime.now(),
        LocalDateTime.now(),
        "location",
        "descritpion",
        false,
        0,
        LocalDateTime.now().toParsedString(
            PATTERN_SERVER
        )
    ) {

    }

    override val type: EventItemType
        get() = EventItemType.EVENT

}

