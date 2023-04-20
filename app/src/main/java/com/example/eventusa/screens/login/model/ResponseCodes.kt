package com.example.eventusa.screens.login.model

enum class ResponseCodes(val value: Int) {

    SUCCESS(0),
    SERVER_ERROR(1),
    WRONG_AUTHENTICATION(2),
    UNKNOWN_ERROR(-1);

    companion object {


        fun get(errorCode: Int): ResponseCodes {
            return when (errorCode) {
                0 -> SUCCESS
                1 -> SERVER_ERROR
                2 -> WRONG_AUTHENTICATION
                else -> UNKNOWN_ERROR
            }

        }
    }

}