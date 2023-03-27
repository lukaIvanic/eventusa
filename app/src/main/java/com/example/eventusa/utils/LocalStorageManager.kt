package com.example.eventusa.utils

import android.content.Context
import android.content.SharedPreferences

object LocalStorageManager {

    private var savedSharedPreferences: SharedPreferences? = null

    private var sharedPreferences: (context: Context) -> SharedPreferences = { context ->
        if (savedSharedPreferences == null) {
            savedSharedPreferences =
                context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        }
        savedSharedPreferences!!
    }


    fun readRememberMe(context: Context): Boolean {
        return sharedPreferences(context).getBoolean("rememberMe", false)
    }

    fun turnOnRememberMe(context: Context){
        val myEdit = sharedPreferences(context).edit()

        myEdit.putBoolean("rememberMe", true)

        myEdit.apply()
    }

    fun turnOffRememberMe(context: Context){
        val myEdit = sharedPreferences(context).edit()

        myEdit.putBoolean("rememberMe", false)

        myEdit.apply()

        saveUserAndPass(context, "", "")

    }



    fun saveUserAndPass(context: Context, user: String, pass: String) {
        val myEdit = sharedPreferences(context).edit()

        myEdit.putString("user", user)
        myEdit.putString("pass", pass)

        myEdit.apply()
    }

    fun readUsername(context: Context): String? {
        return sharedPreferences(context).getString("user", null)
    }

    fun readPassword(context: Context): String? {
        return sharedPreferences(context).getString("pass", null)
    }



//TODO: RENAME
    // Returns true if user checked "Dont ask me again" for "date before today" alert
    fun readAskConfirmDateBefore(context: Context): Boolean {
        return sharedPreferences(context).getBoolean("askAgainDateBefore", true)
    }
    fun setCheckedAskConfirmDateBefore(context: Context){
        val myEdit = sharedPreferences(context).edit()

        myEdit.putBoolean("askAgainDateBefore", true)

        myEdit.apply()
    }

    fun setUncheckedAskConfirmDateBefore(context: Context){
        val myEdit = sharedPreferences(context).edit()

        myEdit.putBoolean("askAgainDateBefore", false)

        myEdit.apply()
    }


}