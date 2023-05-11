package com.example.eventusa.utils.jsonutils

import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.LocalDateTime
import java.time.ZoneOffset


class LocalDateTimeDeserializer: JsonDeserializer<LocalDateTime>() {

    override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): LocalDateTime {
        jp?.let {jp ->
            val timeMillisString =  jp.valueAsString
            try{
                val timeMillis = timeMillisString.toLong()
                return LocalDateTime.ofEpochSecond(timeMillis, 0, ZoneOffset.ofHours(1))
            }catch(e: Exception){ Log.e("LocalDateTimeDeserializer", e.localizedMessage)}

        }



        return LocalDateTime.now()
    }

}
