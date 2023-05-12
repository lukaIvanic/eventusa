package com.example.eventusa.utils.jsonutils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(
        value: LocalDateTime?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?,
    ) {
        value?.let {
            gen?.writeString((it.atZone(ZoneOffset.ofHours(1)).toEpochSecond()).toString())
//            gen?.writeString(it.toParsedString(PATTERN_SERVER))
        }
    }


}