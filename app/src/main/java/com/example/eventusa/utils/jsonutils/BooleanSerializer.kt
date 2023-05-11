package com.example.eventusa.utils.jsonutils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class BooleanSerializer : JsonSerializer<Boolean>() {
    override fun serialize(
        value: Boolean?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?,
    ) {
        if(value == null)   gen?.writeNumber(0)

        if(value == true){
            gen?.writeNumber(1)
        }else{
            gen?.writeNumber(0)

        }

    }


}