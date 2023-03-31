package com.example.eventusa.extensions

fun <T> List<T>.equalsIgnoreOrder(list2: List<T>) =
    this.size == list2.size && this.toSet() == list2.toSet()