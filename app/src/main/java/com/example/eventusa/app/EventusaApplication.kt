package com.example.eventusa.app

import android.app.Application
import com.example.eventusa.ApiKeyHolder
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


        Places.initialize(applicationContext, ApiKeyHolder.GOOGLE_PLACES_API_KEY)
        Places.createClient(this)

        super.onCreate()
    }

}