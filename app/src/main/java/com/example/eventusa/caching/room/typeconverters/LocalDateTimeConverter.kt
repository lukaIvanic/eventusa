package com.example.eventusa.caching.room.typeconverters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

@ProvidedTypeConverter
class LocalDateTimeConverter {

    @TypeConverter
    fun localDateTimeToSeconds(localDateTime: LocalDateTime): Long{
        return localDateTime.atZone(ZoneOffset.ofHours(1)).toEpochSecond()
    }

    @TypeConverter
    fun secondsToLocalDateTime(seconds: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(seconds, 0, (ZoneOffset.ofHours(1)))
    }

}