package com.example.eventusa.app

import android.app.Application
import com.example.eventusa.caching.room.EventusaDatabase
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.repositories.EventsRepository
import com.example.eventusa.repositories.TickHandler
import com.example.eventusa.repositories.UserRepository
import com.example.eventusa.screens.login.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventusaApplication : Application() {

    val eventsRepository = EventsRepository(TickHandler())

    var userRepository = UserRepository()

    override fun onCreate() {

        EventusaDatabase.setupInstance(applicationContext)
        LocalStorageManager.setupSharedPreferences(this)

        val user = LocalStorageManager.readUsername()
        val pass = LocalStorageManager.readPassword()

        if (LocalStorageManager.readRememberMe() && !user.isNullOrEmpty() && !pass.isNullOrEmpty()) {
            userRepository.attemptLogin(User(username = user, pass = pass))
        }

        CoroutineScope(Dispatchers.IO).launch {
//            userRepository.getAllUsers()
        }

        super.onCreate()
    }

}