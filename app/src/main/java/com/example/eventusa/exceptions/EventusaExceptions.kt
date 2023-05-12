package com.example.eventusa.exceptions

import com.example.eventusa.network.ResultOf

enum class EventusaExceptions(val errorMessage: String) {

    NETWORK_EXCEPTION("Please check your network connection."),
    JSON_PARSE_EXCEPTION("An error occured while parsing json."),
    NOT_FOUND_EXCEPTION("Could not find event."),
    INVALID_EVENT_EXCEPTION("The event you submited was invalid."),
    GENERAL_EXCEPTION("An exception occured.");


    operator fun invoke(): ResultOf.Error {
        return ResultOf.Error(Exception(errorMessage))
    }
}