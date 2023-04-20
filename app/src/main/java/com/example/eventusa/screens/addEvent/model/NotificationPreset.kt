package com.example.eventusa.screens.addEvent.data

enum class NotificationPreset(val notifTimeBeforeEventMins: Int, val notifDesc: String?) {

    //TODO STAVIT DA SEMOZE CUSTOM KURAC NAPRAVIT
    //TODO napravit zapravo notification info klasu i ove presete stavit u inner enum te klase

    FIVE_MINUTES(5, "5 minutes before"),
    FIFTEEN_MINUTES(15, "15 minutes before"),
    THIRTY_MINUTES(30, "30 minutes before"),
    SIXTY_MINUTES(60, "60 minutes before");

    companion object {
        fun getPresetsDescs(): Array<String?> {
            return values().map { it.notifDesc }.toTypedArray()
        }

        fun getPresetByIndex(index: Int): NotificationPreset {
            return values()[index]
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

