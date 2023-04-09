package com.example.eventusa.caching.room.typeconverters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@ProvidedTypeConverter
class LocalDateTimeConverter {

    @TypeConverter
    fun localDateTimeToSeconds(localDateTime: LocalDateTime): Long{
        return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    @TypeConverter
    fun secondsToLocalDateTime(seconds: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(seconds, 0, ZoneId.systemDefault().rules.getOffset(
            Instant.now()))
    }

}