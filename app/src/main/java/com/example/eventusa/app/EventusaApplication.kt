package com.example.eventusa.app

import android.app.Application
import com.example.eventusa.caching.room.EventusaDatabase
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.repositories.EventsRepository
import com.example.eventusa.repositories.UserRepository
import com.google.android.libraries.places.api.Places

class EventusaApplication : Application() {

    val eventsRepository = EventsRepository()

    var userRepository = UserRepository()

    override fun onCreate() {

        EventusaDatabase.setupInstance(applicationContext)
        LocalStorageManager.setupSharedPreferences(this)


        val key = "AIzaSyDdewH__Dw3UjqvX75-awW3voVVEeXbH5I"
        Places.initialize(applicationContext, key)
        val placesClient = Places.createClient(this)

        super.onCreate()
    }

}