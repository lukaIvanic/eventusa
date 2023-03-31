package com.example.eventusa.utils

import android.content.Context
import android.content.SharedPreferences

object LocalStorageManager {

    lateinit var sharedPreferences: SharedPreferences

    // initialized in EventusaApplication class
    fun setupSharedPreferences(context: Context) {
        if (this::sharedPreferences.isInitialized.not()) {
            sharedPreferences =
                context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        }
    }

    fun readRememberMe(): Boolean {
        return sharedPreferences.getBoolean("rememberMe", false)
    }

    fun turnOnRememberMe() {
        val myEdit = sharedPreferences.edit()

        myEdit.putBoolean("rememberMe", true)

        myEdit.apply()
    }

    fun turnOffRememberMe() {
        val myEdit = sharedPreferences.edit()

        myEdit.putBoolean("rememberMe", false)

        myEdit.apply()

        saveUserAndPass("", "")

    }


    fun saveUserAndPass(user: String, pass: String) {
        val myEdit = sharedPreferences.edit()

        myEdit.putString("user", user)
        myEdit.putString("pass", pass)

        myEdit.apply()
    }

    fun readUsername(): String? {
        return sharedPreferences.getString("user", null)
    }

    fun readPassword(): String? {
        return sharedPreferences.getString("pass", null)
    }

    fun readAskConfirmDateBefore(): Boolean {
        return sharedPreferences.getBoolean("askAgainDateBefore", true)
    }

    fun setCheckedAskConfirmDateBefore() {
        val myEdit = sharedPreferences.edit()

        myEdit.putBoolean("askAgainDateBefore", true)

        myEdit.apply()
    }

    fun setUncheckedAskConfirmDateBefore() {
        val myEdit = sharedPreferences.edit()

        myEdit.putBoolean("askAgainDateBefore", false)

        myEdit.apply()
    }

    fun addNotification(eventId: Int, notifTimeUntilEventMins: Long): List<Long> {
        val oldNotifsString = sharedPreferences.getString(eventId.toString(), "") ?: ""

        var notifsList = oldNotifsString.savedNotifsToList() as MutableList
        notifsList.add(notifTimeUntilEventMins)

        val newNotifsString = notifsList.savedNotifsToString()
        saveNotifications(eventId, newNotifsString)
        return notifsList
    }

    fun readNotifications(eventId: Int): List<Long> {
        val notifsString = sharedPreferences.getString(eventId.toString(), "") ?: ""
        return notifsString.savedNotifsToList()
    }

    fun deleteNotification(eventId: Int, notifTimeUntilEventMins: Long) {
        val oldNotifsString = sharedPreferences.getString(eventId.toString(), "") ?: ""

        var notifsList = oldNotifsString.savedNotifsToList() as MutableList
        notifsList.remove(notifTimeUntilEventMins)

        val newNotifsString = notifsList.savedNotifsToString()
        saveNotifications(eventId, newNotifsString)
    }

    fun saveNotifications(eventId: Int, notifsString: String) {
        val myEdit = sharedPreferences.edit()

        myEdit.putString(eventId.toString(), notifsString)

        myEdit.apply()


    }

    fun String.savedNotifsToList(): List<Long> {

        if (this.trim().isEmpty()) return ArrayList()

        return this.split(" ").map { it.toLong() }

    }


    fun List<Long>.savedNotifsToString(): String {
        var string = ""
        this.forEach {
            string += "$it "
        }
        return string.trim()
    }


}