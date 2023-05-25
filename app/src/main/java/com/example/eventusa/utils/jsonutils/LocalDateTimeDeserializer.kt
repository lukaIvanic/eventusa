package com.example.eventusa.utils.jsonutils

import android.util.Log
import com.example.eventusa.utils.extensions.PATTERN_SERVER
import com.example.eventusa.utils.extensions.toLocalDateTime
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.LocalDateTime


class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {

    override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): LocalDateTime {
        jp?.let { jp ->
            try {
                return jp.valueAsString.toLocalDateTime(PATTERN_SERVER)
            } catch (e: Exception) {
                Log.e("LocalDateTimeDeserializer", e.localizedMessage)
            }

        }

        return LocalDateTime.now()
    }

}
