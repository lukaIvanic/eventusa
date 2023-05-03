package com.example.eventusa.screens.events.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eventusa.R
import com.example.eventusa.screens.addEvent.view.AddEventActivity.*
import com.example.eventusa.screens.login.model.User
import com.example.eventusa.utils.extensions.PATTERN_SERVER
import com.example.eventusa.utils.extensions.toParsedString
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import kotlin.random.Random


/**
 *
 * [usersAttending] list is used to create a relation with users, instead of a one to many relation.
 * This way, we don't have the direct user - events references, but it's not necessary,
 * we can just filter through all events,  whose size usually won't even reach two digits.
 *
 * @param [isInSeries] true if the event last multiple days and was separated into each day for display in list
 */
@Entity("rinetevents_table")
data class RINetEvent(

    @PrimaryKey(autoGenerate = true) var eventId: Int = 0,


    @JsonProperty("Name") var title: String = "",


    @JsonProperty("From") var startDateTime: LocalDateTime = LocalDateTime.now(),


    @JsonProperty("To") var endDateTime: LocalDateTime = LocalDateTime.now(),


    var location: String? = "",

    var description: String? = "",

    var calendar: Boolean? = false,

    var days: Int? = 0,

    var dateAddded: String? = LocalDateTime.now().toParsedString(PATTERN_SERVER),

    var usersAttending: MutableList<User> = ArrayList(),

    var isInSeries: Boolean = false,

    var isFirstInSeries: Boolean = false,

    var isLastInSeries: Boolean = false,

    var eventColor: Int = EventColors.randomColor(),

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
        ),


        ) {

    }

    override val type: EventItemType
        get() = EventItemType.EVENT

    override fun equals(other: Any?): Boolean {
        val otherEvent = other as? RINetEvent ?: return false


        return eventId == otherEvent.eventId
                && title == otherEvent.title
                && startDateTime == otherEvent.startDateTime
                && endDateTime == otherEvent.endDateTime
                && location == otherEvent.location
                && description == otherEvent.description
                && calendar == otherEvent.calendar
                && usersAttending.toSet() == otherEvent.usersAttending.toSet()
                && eventColor == otherEvent.eventColor
    }

}

object EventColors {
    const val YELLOW = 0
    const val RED = 1
    const val DARK_BLUE = 2
    const val RINET_BLUE = 3
    const val LIGHT_PURPLE = 4
    const val GREEN = 5
    const val GRAY = 6
    const val ORANGE = 7
    const val BROWN = 8
    const val DARK_RED = 9

    fun getPresets(): Array<String> {
        return arrayOf(
            "Yellow",
            "Red",
            "Dark Blue",
            "Rinet Blue",
            "Light Purple",
            "Green",
            "Gray",
            "Orange",
            "Brown",
            "Dark red"
        )
    }

    fun getColorId(eventColor: Int): Int {
        return when (eventColor) {
            YELLOW -> {
                R.color.event_yellow
            }
            RED -> {
                R.color.event_red
            }
            DARK_BLUE -> {
                R.color.event_dark_blue
            }
            RINET_BLUE -> {
                R.color.event_rinet_blue
            }
            LIGHT_PURPLE -> {
                R.color.event_light_purple
            }
            GREEN -> {
                R.color.event_dark_green
            }
            GRAY -> {
                R.color.event_dark_gray
            }
            ORANGE -> {
                R.color.event_darker_orange
            }
            BROWN -> {
                R.color.event_brown
            }
            DARK_RED -> {
                R.color.event_dark_red
            }
            else -> {
                return R.color.event_rinet_blue
            }
        }
    }

    fun getDrawableIdEmpty(eventColor: Int): Int {
        return when (eventColor) {
            YELLOW -> {
                R.drawable.color_option_empty_yellow
            }
            RED -> {
                R.drawable.color_option_empty_red
            }
            DARK_BLUE -> {
                R.drawable.color_option_empty_dark_blue
            }
            RINET_BLUE -> {
                R.drawable.color_option_empty_rinet_blue
            }
            LIGHT_PURPLE -> {
                R.drawable.color_option_empty_light_purple
            }
            GREEN -> {
                R.drawable.color_option_empty_green
            }
            GRAY -> {
                R.drawable.color_option_empty_gray
            }
            ORANGE -> {
                R.drawable.color_option_empty_orange
            }
            BROWN -> {
                R.drawable.color_option_empty_brown
            }
            DARK_RED -> {
                R.drawable.color_option_empty_dark_red
            }
            else -> {
                return R.drawable.color_option_empty_rinet_blue
            }
        }

    }

    fun getDrawableIdFull(eventColor: Int): Int {




        return when (eventColor) {
            YELLOW -> {
                R.drawable.color_option_full_yellow
            }
            RED -> {
                R.drawable.color_option_full_red
            }
            DARK_BLUE -> {
                R.drawable.color_option_full_dark_blue
            }
            RINET_BLUE -> {
                R.drawable.color_option_full_rinet_blue
            }
            LIGHT_PURPLE -> {
                R.drawable.color_option_full_light_purple
            }
            GREEN -> {
                R.drawable.color_option_full_green
            }
            GRAY -> {
                R.drawable.color_option_full_gray
            }
            ORANGE -> {
                R.drawable.color_option_full_orange
            }
            BROWN -> {
                R.drawable.color_option_full_brown
            }
            DARK_RED -> {
                R.drawable.color_option_full_dark_red
            }
            else -> {
                return R.drawable.color_option_full_rinet_blue
            }

        }
    }

    fun randomColor(): Int {
        return Random.nextInt(0, 9)
    }

    fun getColorItems(): Array<Item> {

        val presetsText = getPresets()

        var array: Array<Item> = presetsText.mapIndexed { indx, text ->
            Item(text, getDrawableIdEmpty(indx))
        }.toTypedArray()


        return array
    }

}
