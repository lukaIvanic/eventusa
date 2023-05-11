package com.example.eventusa.repositories

import com.example.eventusa.network.Network
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.utils.extensions.doIfSucces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


/**
 * Creates an observable tick in intervals.
 * Observers request new data on every tick
 * @param intervalMilis defines repeating interval length in milliseconds.
 */
class TickHandlerLocal(
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val intervalMilis: Long = 5000000,
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
 * Repository for all events, but only connects with room db (local storage).
 */
class EventsRepositoryLocal(
    private val tickHandler: TickHandlerLocal,
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
     * Events source
     */
    private var _events: MutableSharedFlow<ResultOf<MutableList<RINetEvent>>> = MutableSharedFlow()


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

    public suspend fun makeEventsUpdate(){
        refreshEvents()
    }

    private suspend fun refreshEvents() {
        externalScope.launch {
            _events.emit(ResultOf.Loading)
            try {
                val newEvents: MutableList<RINetEvent> = Network.getEvents()
//            val newEvents: MutableList<RINetEvent> = Room.readAllEvents() as MutableList
                _events.emit(ResultOf.Success(newEvents))
            } catch (e: Exception) {
                _events.emit(ResultOf.Error(e))
            }
        }

    }

    // Create
//    suspend fun addEvent(rinetEvent: RINetEvent) {
//        Room.insertEvent(rinetEvent)
//    }

    // Read
    fun getEventWithId(eventId: Int): ResultOf<RINetEvent> {

        if(cachedSuccessEvents.replayCache.isEmpty()){
            runBlocking {
                refreshEvents()
            }
        }


        cachedSuccessEvents.replayCache
            .last()
            .doIfSucces { latestCachedEvents ->
                latestCachedEvents.forEach {
                    if (it.eventId == eventId) return ResultOf.Success(it)
                }
            }

        return ResultOf.Error(Exception("Couldn't find event, was it deleted recently?"))

    }

//    suspend fun getEventRoom(eventId: Int): RINetEvent{
//        return Room.readEvent(eventId)
//    }

    // Update
//    suspend fun updateEvent(rinetEvent: RINetEvent){
//        Room.updateEvent(rinetEvent)
//    }

    // Delete
//    suspend fun deleteEvent(eventId: Int){
//        Room.deleteEvent(eventId)
//    }



    fun makeUpdateTick() {
        tickHandler.makeTick()
    }


}
