package com.example.eventusa.screens.addEvent.model


/**
 * Used when defining reminder times for an event.
 */
enum class NotificationPreset(val notifTimeBeforeEventMins: Long, val notifDesc: String?) {

    //TODO STAVIT DA SEMOZE CUSTOM VRIJEME NAPRAVIT
    //TODO napravit zapravo notification info klasu i ove presete stavit u inner enum te klase

    FIVE_MINUTES(5, "5 minutes before"),
    FIFTEEN_MINUTES(15, "15 minutes before"),
    THIRTY_MINUTES(30, "30 minutes before"),
    SIXTY_MINUTES(60, "60 minutes before");

    companion object {

        fun getPresetsDescriptions(): Array<String?> {
            return values().map { it.notifDesc }.toTypedArray()
        }

        fun getPresetByIndex(index: Int): NotificationPreset {
            return values()[index]
        }

        fun get(notifTimeBeforeEventMins: Long): NotificationPreset? {
            return when (notifTimeBeforeEventMins) {
                5L -> {
                    FIVE_MINUTES
                }
                15L -> {
                    FIFTEEN_MINUTES
                }
                30L -> {
                    THIRTY_MINUTES
                }
                60L -> {
                    SIXTY_MINUTES
                }
                else -> {
                    null
                }
            }

        }
    }


}

