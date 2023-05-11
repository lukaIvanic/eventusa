package com.example.eventusa.exceptions

import com.example.eventusa.network.ResultOf

enum class EventusaExceptions(val errorMessage: String) {

    NETWORK_EXCEPTION("A network error occured."),
    JSON_PARSE_EXCEPTION("An error occured while parsing json.");


    operator fun invoke(): ResultOf.Error {
        return ResultOf.Error(java.lang.Exception(errorMessage))
    }
}