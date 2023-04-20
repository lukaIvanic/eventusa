package com.example.eventusa.utils

fun main() {
    val minsUntilEvent = (60 * 24) % 1000 // minute od notifikacije do pocetka eventa,
                                // npr ovo predstavlja reminder 24 sata prije eventa

    val eventId = 999999 % 1000 // Id sa servera, moze ici u "beskonacnost"

    val generatedNotifId = "$eventId${minsUntilEvent}".toInt() // Ispada 9999991440, sto je preveliko za int

    print("notifId: $generatedNotifId")
}