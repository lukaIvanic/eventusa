package com.example.eventusa.utils.extensions

import com.example.eventusa.screens.events.data.RINetEvent

fun List<RINetEvent>.copy(): MutableList<RINetEvent> {
    return ArrayList(this.map { it.copy() })
}