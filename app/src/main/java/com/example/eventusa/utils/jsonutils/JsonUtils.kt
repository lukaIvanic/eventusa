package com.example.eventusa.utils.jsonutils

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime


object JsonUtils {

    val mapper = jacksonObjectMapper()
//        .setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE)
        .registerModule(JavaTimeModule())
        .registerModule(SimpleModule()
            .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer())
            .addSerializer(Boolean::class.java, BooleanSerializer())
            .addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())
            .addDeserializer(Boolean::class.java, BooleanDeserializer())

        )

    inline fun <reified T> fromJsonToList(jsonString: String): MutableList<T> {
        val listType: CollectionType =
            mapper.getTypeFactory().constructCollectionType(MutableList::class.java, T::class.java)
        return mapper.readValue(jsonString, listType)
    }

    inline fun <reified T> fromJsonToObject(jsonString: String): T {
        return mapper.readValue(jsonString, T::class.java)
    }

    fun <T> toJson(data: T): String {
        val events = mapper.writeValueAsString(data)
        return events
    }


}