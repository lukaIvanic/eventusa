package com.example.eventusa.network

sealed class ResultOf<out T> {
    data class Success<out T>(val data: T) : ResultOf<T>()
    data class Error(val exception: Exception) : ResultOf<Nothing>()
    object Loading : ResultOf<Nothing>()
}