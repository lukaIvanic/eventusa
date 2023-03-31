package com.example.eventusa.repository

import com.example.eventusa.extensions.doIfSucces
import com.example.eventusa.network.Network
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.events.data.RINetEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Creates an observable tick in intervals.
 * Observers request new data on every tick
 * @param intervalMilis defines repeating interval length in milliseconds.
 */
class TickHandler(
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val intervalMilis: Long = 5000,
) {

    val tickFlow: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)

    init {
        externalScope.launch {
            updateTick()
        }
    }

    fun makeTick() {
        externalScope.launch {
            tickFlow.emit(Unit)
        }
    }

    /**
     * Creates a new tick immediately.
     */
    private suspend fun updateTick() {
        tickFlow.emit(Unit)
        delay(intervalMilis)
        updateTick()
    }


}

/**
 * Repository for all events.
 */
class EventsRepository(
    private val tickHandler: TickHandler,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) {

    init {
        externalScope.launch {
            tickHandler.tickFlow.collect {
                refreshEvents()
            }
        }
    }

    /**
     * Source events
     */
    private val _events by lazy { MutableSharedFlow<ResultOf<MutableList<RINetEvent>>>() }

    /**
     * Abstraction necessary for retrieving recent data
     */
    private val cachedSuccessEvents =
        _events
            .filter { it is ResultOf.Success }
            .map { it as ResultOf.Success }
            .shareIn(
                externalScope,
                SharingStarted.Eagerly,
                1
            )

    /**
     * Used as a constant stream of events data.
     */
    val currentEventsResult =
        _events
            .shareIn(
                externalScope,
                SharingStarted.WhileSubscribed(4000, Long.MAX_VALUE),
                1
            )


    private suspend fun refreshEvents() {
        _events.emit(ResultOf.Loading)
        try {
            val newEvents = Network.getEvents()
            _events.emit(ResultOf.Success(newEvents))
        } catch (e: Exception) {
            _events.emit(ResultOf.Error(e))
        }
    }

    fun getEventWithId(eventId: Int): ResultOf<RINetEvent> {

        cachedSuccessEvents.replayCache
            .last()
            .doIfSucces { latestCachedEvents ->
                latestCachedEvents.forEach {
                    if (it.eventId == eventId) return ResultOf.Success(it)
                }
            }

        return ResultOf.Error(Exception("Couldn't find event, was it deleted recently?"))

    }

    fun makeUpdateTick() {
        tickHandler.makeTick()
    }


}
