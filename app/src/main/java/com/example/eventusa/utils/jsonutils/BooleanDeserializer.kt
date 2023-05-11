package com.example.eventusa.utils.jsonutils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class BooleanDeserializer: JsonDeserializer<Boolean>() {

    override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): Boolean {
        jp?.let {jp ->
            val boolString =  jp.valueAsString
           return if(boolString == "1") true else false

        }

        return false
    }

}
