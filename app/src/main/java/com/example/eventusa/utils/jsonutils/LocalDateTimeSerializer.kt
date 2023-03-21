package com.example.eventusa.utils.jsonutils

import com.example.eventusa.extensions.PATTERN_SERVER
import com.example.eventusa.extensions.toParsedString
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.LocalDateTime

class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    override fun serialize(
        value: LocalDateTime?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?,
    ) {
        value?.let {
            gen?.writeString(it.toParsedString(PATTERN_SERVER))
        }
    }


}