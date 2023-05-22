package com.example.eventusa.network

/**
 * ResultOf
 * Wrapper sealed class for all communications between repositories - viewmodels - views.
 * @see com.example.eventusa.extensions.doIfSucces
 * @see com.example.eventusa.extensions.doIfLoading
 * @see com.example.eventusa.extensions.doIfFailure
 * @see com.example.eventusa.extensions.map
 */
sealed class ResultOf<out T> {
    data class Success<out T>(val data: T) : ResultOf<T>()
    data class Error(val exception: Exception) : ResultOf<Nothing>()
    object Loading : ResultOf<Nothing>()

    override fun equals(other: Any?): Boolean {
        return false
    }
}