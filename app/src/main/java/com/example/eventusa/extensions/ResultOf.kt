package com.example.eventusa.extensions

import com.example.eventusa.network.ResultOf

inline fun <reified T> ResultOf<T>.doIfFailure(callback: (error: Exception) -> Unit) {
    if (this is ResultOf.Error) {
        callback(this.exception)
    }
}

inline fun <reified T> ResultOf<T>.doIfSucces(callback: (value: T) -> Unit) {
    if (this is ResultOf.Success) {
        callback(this.data)
    }
}

inline fun <reified T> ResultOf<T>.doIfLoading(callback: () -> Unit) {
    if (this is ResultOf.Loading) {
        callback()
    }
}

inline fun <reified T, reified R> ResultOf<T>.map(transform: (T) -> R): ResultOf<R> {
    return when (this) {
        is ResultOf.Success -> {
            try{ResultOf.Success(transform(data))}
            catch (e: Exception) {ResultOf.Error(e)}
        }
        is ResultOf.Error -> this
        is ResultOf.Loading -> this
    }
}

inline fun <reified T, reified R> ResultOf<T>.adjustType(): ResultOf<R> {
    return when (this) {
        is ResultOf.Success, is ResultOf.Loading -> {
            ResultOf.Error(Exception("Not possible, ignore"))
        }
        is ResultOf.Error -> this
    }
}