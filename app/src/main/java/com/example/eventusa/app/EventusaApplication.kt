package com.example.eventusa.app

import android.app.Application
import com.example.eventusa.repository.EventsRepository
import com.example.eventusa.repository.TickHandler
import com.example.eventusa.repository.UserRepository

class EventusaApplication: Application() {

    val eventsRepository = EventsRepository(TickHandler())
    var userRepository = UserRepository()

}