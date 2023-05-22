package com.example.eventusa.app

import android.app.Application
import com.example.eventusa.caching.room.EventusaDatabase
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.repositories.EventsRepository
import com.example.eventusa.repositories.UserRepository

class EventusaApplication : Application() {

    val eventsRepository = EventsRepository()

    var userRepository = UserRepository()

    override fun onCreate() {

        EventusaDatabase.setupInstance(applicationContext)
        LocalStorageManager.setupSharedPreferences(this)
        super.onCreate()
    }

}