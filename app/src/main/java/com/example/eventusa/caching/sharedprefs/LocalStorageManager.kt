package com.example.eventusa.caching.sharedprefs

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

    fun readShowMultipleDayEvents(): Boolean {
        return sharedPreferences.getBoolean("showMultipleDayEvents", false)
    }

    fun setCheckedShowMultipleDayEvents() {
        val myEdit = sharedPreferences.edit()

        myEdit.putBoolean("showMultipleDayEvents", true)

        myEdit.apply()
    }

    fun setUncheckedShowMultipleDayEvents() {
        val myEdit = sharedPreferences.edit()

        myEdit.putBoolean("showMultipleDayEvents", false)

        myEdit.apply()
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

}