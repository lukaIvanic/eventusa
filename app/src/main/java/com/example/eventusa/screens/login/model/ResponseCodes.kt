package com.example.eventusa.screens.login.model

enum class ResponseCodes(val value: Int) {

    SUCCESS(0),
    SERVER_ERROR(-1),
    WRONG_AUTHENTICATION(-2);

    companion object {
        fun get(value: Int): ResponseCodes? {
            return when(value){
                0 -> SUCCESS
                -1 -> SERVER_ERROR
                -2 -> WRONG_AUTHENTICATION
                else -> null
            }
        }
    }


}