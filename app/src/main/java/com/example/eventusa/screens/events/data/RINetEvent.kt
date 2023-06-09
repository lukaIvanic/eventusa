package com.example.eventusa.screens.events.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eventusa.R
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.screens.addEvent.view.activity.AddEventActivity.*
import com.example.eventusa.screens.addEvent.view.activity.Item
import com.example.eventusa.screens.login.model.User
import com.fasterxml.jackson.annotation.JsonIgnore
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

    @PrimaryKey(autoGenerate = true)
    @JsonProperty("idEventa")
    var eventId: Int = 0,

    @JsonProperty("naslov")
    var title: String = "",

    @JsonProperty("datumVrijemeOd")
    var startDateTime: LocalDateTime = LocalDateTime.now(),

    @JsonProperty("datumVrijemeDo")
    var endDateTime: LocalDateTime = LocalDateTime.now(),

    @JsonProperty("lokacija")
    var location: String? = "",

    @JsonProperty("opis")
    var summary: String? = "",

    @JsonProperty("kalendar")
    var isInCalendar: Boolean = false,

    @JsonIgnore
    var usersAttending: MutableList<User> = ArrayList(),

    @JsonProperty("userIdsList")
    var userIdsStringList: String? = "",

    var eventColor: Int = EventColors.RINET_BLUE,

    @JsonIgnore
    var isInSeries: Boolean = false,

    @JsonIgnore
    var isFirstInSeries: Boolean = false,

    @JsonIgnore
    var isLastInSeries: Boolean = false,


    @JsonIgnore
    var isFirstInDate: Boolean = true,

    ) : EventItem {

//            get() {
//                var s = ""
//                usersAttending.dropLast(1).forEach {
//                    s += "$it, "
//                }
//
//                usersAttending.lastOrNull()?.let {
//                    s += it
//                }
//
//                return s
//            }



        constructor() : this(
        0,
        "name",
        LocalDateTime.now(),
        LocalDateTime.now(),
        "location",
        "descritpion",
        false,


        ) {

    }

    @get:JsonIgnore
    override val type: EventItemType
        get() = EventItemType.EVENT

    override fun equals(other: Any?): Boolean {
        val otherEvent = other as? RINetEvent ?: return false


        return eventId == otherEvent.eventId
                && title == otherEvent.title
                && startDateTime.toLocalDate() == otherEvent.startDateTime.toLocalDate()
                && startDateTime.hour == otherEvent.startDateTime.hour
                && startDateTime.minute == otherEvent.startDateTime.minute
                && endDateTime == otherEvent.endDateTime
                && location == otherEvent.location
                && summary == otherEvent.summary
                && isInCalendar == otherEvent.isInCalendar
                && usersAttending?.toSet() == otherEvent.usersAttending?.toSet()
                && eventColor == otherEvent.eventColor
                && isFirstInDate == otherEvent.isFirstInDate
                && isInSeries == otherEvent.isInSeries
                && isFirstInSeries == otherEvent.isFirstInSeries
                && isLastInSeries == otherEvent.isLastInSeries
    }

}

object EventColors {
    const val RINET_BLUE = 0
    const val RED = 1
    const val DARK_BLUE = 2
    const val YELLOW = 3
    const val LIGHT_PURPLE = 4
    const val GREEN = 5
    const val GRAY = 6
    const val ORANGE = 7
    const val BROWN = 8
    const val DARK_RED = 9

    fun getRandomColor(): Int{
        return Random.nextInt(0, 10)
    }
    fun getPresets(): Array<String> {
        return arrayOf(
            "Rinet Blue",
            "Red",
            "Dark Blue",
            "Yellow",
            "Light Purple",
            "Green",
            "Gray",
            "Orange",
            "Brown",
            "Dark red"
        )
    }

    fun getColorId(eventColor: Int): Int {

        if(!LocalStorageManager.readShowFullColorsInApp()) return R.color.event_rinet_blue

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

        if(!LocalStorageManager.readShowFullColorsInApp()) return R.drawable.color_option_empty_rinet_blue


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

        if(!LocalStorageManager.readShowFullColorsInApp()) return R.drawable.color_option_full_rinet_blue

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

    fun getColorItems(): Array<Item> {

        val presetsText = getPresets()

        var array: Array<Item> = presetsText.mapIndexed { indx, text ->
            Item(text, getDrawableIdEmpty(indx))
        }.toTypedArray()


        return array
    }

}
