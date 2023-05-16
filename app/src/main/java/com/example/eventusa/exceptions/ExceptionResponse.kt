package com.example.eventusa.exceptions

import com.example.eventusa.exceptions.EventusaExceptions.*
import com.example.eventusa.network.ResultOf

/**
 * @param [type] and [traceId] are not used and ignored.
 */
data class ExceptionResponse(
    val title: String,
    val status: Int,
    val type: Any? = null,
    val traceId: Any? = null

) {

    fun getException(): ResultOf.Error {
        return when(status){
            400 -> INVALID_EVENT_EXCEPTION()
            401 -> UNAUTHORIZED_EXCEPTION()
            404 -> NOT_FOUND_EXCEPTION()
            else -> GENERAL_EXCEPTION()
        }
    }

}