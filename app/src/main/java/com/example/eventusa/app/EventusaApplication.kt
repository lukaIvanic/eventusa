package com.example.eventusa.app

import android.app.Application
import com.example.eventusa.caching.room.EventusaDatabase
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.repositories.EventsRepository
import com.example.eventusa.repositories.TickHandler
import com.example.eventusa.repositories.UserRepository
import com.example.eventusa.screens.login.model.LoginRequest

class EventusaApplication : Application() {

    val eventsRepository = EventsRepository(TickHandler())

    var userRepository = UserRepository()

    override fun onCreate() {

        EventusaDatabase.setupInstance(applicationContext)
        LocalStorageManager.setupSharedPreferences(this)

        val user = LocalStorageManager.readUsername()
        val pass = LocalStorageManager.readPassword()

        if (!user.isNullOrEmpty() && !pass.isNullOrEmpty()) {
            userRepository.attemptLogin(LoginRequest(user, pass))
        }
        super.onCreate()
    }

}