package com.example.eventusa.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.eventusa.utils.LocalStorageManager.sharedPreferences

/** LocalStorageManager
 *  API for interacting with shared preferences.
 *  Provides precise methods for storage operations.
 *  @property sharedPreferences gets initialized in EventusaApplication
 *
 */
object LocalStorageManager {

    lateinit var sharedPreferences: SharedPreferences

    /**
     * Initialized in EventusaApplication class
     */
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
        return sharedPreferences.getBoolean("askConfirmDateBefore", true)
    }

    fun setCheckedAskConfirmDateBefore() {
        val myEdit = sharedPreferences.edit()

        myEdit.putBoolean("askConfirmDateBefore", true)

        myEdit.apply()
    }

    fun setUncheckedAskConfirmDateBefore() {
        val myEdit = sharedPreferences.edit()

        myEdit.putBoolean("askConfirmDateBefore", false)

        myEdit.apply()
    }

    /**
     * Reads current saved notifications and saves current plus new notification.
     */
    fun addNotification(eventId: Int, notifTimeUntilEventMins: Long): List<Long> {
        val notifsString = sharedPreferences.getString(eventId.toString(), "") ?: ""

        var notifsList = notifsString.savedNotifsStringToList() as MutableList
        notifsList.add(notifTimeUntilEventMins)

        val newNotifsString = notifsList.savedNotifsListToString()
        saveNotifications(eventId, newNotifsString)
        return notifsList
    }

    fun readNotifications(eventId: Int): List<Long> {
        val notifsString = sharedPreferences.getString(eventId.toString(), "") ?: ""
        return notifsString.savedNotifsStringToList()
    }

    fun deleteNotification(eventId: Int, notifTimeUntilEventMins: Long) {
        val notifsString = sharedPreferences.getString(eventId.toString(), "") ?: ""

        var notifsList = notifsString.savedNotifsStringToList() as MutableList
        notifsList.remove(notifTimeUntilEventMins)

        val newNotifsString = notifsList.savedNotifsListToString()
        saveNotifications(eventId, newNotifsString)
    }

    private fun saveNotifications(eventId: Int, notifsString: String) {
        val myEdit = sharedPreferences.edit()

        myEdit.putString(eventId.toString(), notifsString)

        myEdit.apply()


    }

    /**
     * savedNotifsStringToList and savedNotifsListToString.
     * Custom serializer of events and notification for that event. One (event) to many (notifications) relation.
     */

    fun String.savedNotifsStringToList(): List<Long> {

        if (this.trim().isEmpty()) return ArrayList()

        return this.split(" ").map { it.toLong() }

    }


    fun List<Long>.savedNotifsListToString(): String {
        var string = ""
        this.forEach {
            string += "$it "
        }
        return string.trim()
    }


}