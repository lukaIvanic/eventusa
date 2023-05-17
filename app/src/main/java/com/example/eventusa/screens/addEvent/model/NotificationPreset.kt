package com.example.eventusa.screens.addEvent.data

import com.example.eventusa.caching.room.extraentities.EventNotification

enum class NotificationPreset(val notifTimeBeforeEventMins: Int, val notifDesc: String?) {

    //TODO STAVIT DA SEMOZE CUSTOM KURAC NAPRAVIT
    //TODO napravit zapravo notification info klasu i ove presete stavit u inner enum te klase

    FIVE_MINUTES(5, "5 minutes before"),
    FIFTEEN_MINUTES(15, "15 minutes before"),
    THIRTY_MINUTES(30, "30 minutes before"),
    SIXTY_MINUTES(60, "60 minutes before");

    companion object {
        fun getPresetsDescs(existingNotifs: List<EventNotification>): Array<String?> {
            return values().filter { preset ->
                !existingNotifs.map { it.minutesBeforeEvent }
                    .contains(preset.notifTimeBeforeEventMins)
            }.map {
                it.notifDesc
            }.toTypedArray()
        }

        fun getPresetByIndex(
            index: Int,
            existingNotifs: List<EventNotification> = ArrayList(),
        ): NotificationPreset {
            return values().filter { preset ->
                !existingNotifs.map { it.minutesBeforeEvent }
                    .contains(preset.notifTimeBeforeEventMins)
            }[index]
        }

        fun get(notifTimeBeforeEventMins: Int): NotificationPreset? {
            return when (notifTimeBeforeEventMins) {
                5 -> {
                    FIVE_MINUTES
                }
                15 -> {
                    FIFTEEN_MINUTES
                }
                30 -> {
                    THIRTY_MINUTES
                }
                60 -> {
                    SIXTY_MINUTES
                }
                else -> {
                    null
                }
            }

        }
    }


}

