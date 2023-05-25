package com.example.eventusa.repositories

import com.example.eventusa.network.Network
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.utils.extensions.copy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for all events.
 */
class EventsRepository(
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) {

    init {
        fetchEvents()
    }

    /**
     * Events source
     */
    private var _eventsResult: MutableStateFlow<ResultOf<MutableList<RINetEvent>>> =
        MutableStateFlow(ResultOf.Loading)
    val eventsResult = _eventsResult.asStateFlow()

    /**
     * Memory cache
     */
    private var cachedSuccessEvents: MutableList<RINetEvent>? = null


    private fun fetchEvents() = ioScope.launch {
        _eventsResult.emit(ResultOf.Loading)
        try {
            val newEventsResultOf = Network.getEvents()
            _eventsResult.emit(newEventsResultOf)
            if (newEventsResultOf is ResultOf.Success) {
                cachedSuccessEvents = newEventsResultOf.data
            }
        } catch (e: Exception) {
            _eventsResult.emit(ResultOf.Error(e))
        }
    }

    suspend fun addEvent(rinetEvent: RINetEvent): ResultOf<RINetEvent> =
        withContext(ioScope.coroutineContext) {
            val insertResultOf = Network.insertEvent(rinetEvent)

            if (insertResultOf is ResultOf.Success) {
                insertEventLocally(insertResultOf.data)
            }

            return@withContext insertResultOf
        }

    private suspend fun insertEventLocally(rinetEvent: RINetEvent) =
        ioScope.launch {
            cachedSuccessEvents?.add(rinetEvent)
            cachedSuccessEvents?.let {
                _eventsResult.emit(ResultOf.Loading)
                delay(500) // The animation looks nicer with a delay
                _eventsResult.emit(ResultOf.Success(ArrayList(it).copy()).copy())
            }
        }

    suspend fun updateEvent(rinetEvent: RINetEvent): ResultOf<Boolean> =
        withContext(ioScope.coroutineContext) {
            val updateResultOf = Network.updateEvent(rinetEvent)

            if (updateResultOf is ResultOf.Success) {
                updateEventLocally(rinetEvent.eventId, ResultOf.Success(rinetEvent))
            }

            return@withContext updateResultOf
        }


    fun getCachedEventWithId(eventId: Int): RINetEvent? =
        cachedSuccessEvents?.find { it.eventId == eventId }


    suspend fun getEventWithId(eventId: Int): ResultOf<RINetEvent> =
        withContext(ioScope.coroutineContext) {
            val eventResult = Network.getEvent(eventId)

            updateEventLocally(eventId, eventResult)

            return@withContext eventResult
        }

    private fun updateEventLocally(eventId: Int, eventResult: ResultOf<RINetEvent>) =
        ioScope.launch {
            if (eventResult is ResultOf.Success) {

                val cachedEvent = cachedSuccessEvents?.find { it.eventId == eventId }

                if (cachedEvent == eventResult.data) return@launch

                cachedSuccessEvents?.removeIf { it.eventId == eventId }
                cachedSuccessEvents?.add(eventResult.data)
                cachedSuccessEvents?.let {
                    _eventsResult.emit(ResultOf.Loading)
                    delay(500) // The animation looks nicer with a delay
                    _eventsResult.emit(ResultOf.Success(ArrayList(it).copy()).copy())
                }
            }
        }

    suspend fun deleteEvent(eventId: Int): ResultOf<Unit> =
        withContext(ioScope.coroutineContext) {
            val deleteResult = Network.deleteEvent(eventId)
            if (deleteResult is ResultOf.Success) {
                deleteEventLocally(eventId)
            }
            return@withContext deleteResult
        }

    private fun deleteEventLocally(eventId: Int) = ioScope.launch {

        cachedSuccessEvents?.removeIf { it.eventId == eventId }
        cachedSuccessEvents?.let {
            _eventsResult.emit(ResultOf.Loading)
            delay(500) // The animation looks nicer with a delay
            _eventsResult.emit(ResultOf.Success(ArrayList(it).copy()).copy())
        }

    }

    fun refreshEvents() {
        if (eventsResult.value is ResultOf.Loading) return
        fetchEvents()
    }


}
