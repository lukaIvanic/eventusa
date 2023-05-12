package com.example.eventusa.exceptions

import com.example.eventusa.exceptions.EventusaExceptions.*
import com.example.eventusa.network.ResultOf


data class ExceptionResponse(
    val title: String,
    val status: Int,
) {

    fun getException(): ResultOf.Error {
        return when(status){
            400 -> INVALID_EVENT_EXCEPTION()
            404 -> NOT_FOUND_EXCEPTION()
            else -> GENERAL_EXCEPTION()
        }
    }

}