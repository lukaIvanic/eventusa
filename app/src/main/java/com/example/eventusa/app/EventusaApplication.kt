package com.example.eventusa.app

import android.app.Application
import com.example.eventusa.repository.EventsRepository
import com.example.eventusa.repository.TickHandler
import com.example.eventusa.repository.UserRepository
import com.example.eventusa.utils.LocalStorageManager

/**
 * Application extension for dependency injection alternative
 */
class EventusaApplication: Application() {

    val eventsRepository = EventsRepository(TickHandler())

    var userRepository = UserRepository()

    override fun onCreate() {
        LocalStorageManager.setupSharedPreferences(this)
        super.onCreate()
    }

}