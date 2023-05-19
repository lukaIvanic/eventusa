package com.example.eventusa.repositories

import com.example.eventusa.network.Network
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.utils.extensions.doIfSucces
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime

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


    private suspend fun refreshEvents() {
        externalScope.launch {
            _events.emit(ResultOf.Loading)
            try {
                val newEventsResultOf = Network.getEvents()
                _events.emit(newEventsResultOf)
            } catch (e: Exception) {
                _events.emit(ResultOf.Error(e))
            }
        }
    }

    private val dummyList: MutableList<RINetEvent> by lazy { ArrayList<RINetEvent>() }

    private fun getDummyData(): List<RINetEvent> {
        if (dummyList.isEmpty()) {
            dummyList.addAll(generateEvents())
        }
        return dummyList
    }

    private fun generateEvents(): List<RINetEvent> {
        return arrayListOf(
            RINetEvent(
                eventId = 10,
                title = "Sastanak",
                startDateTime = LocalDateTime.now().withMonth(4)
                    .withDayOfMonth(6)
                    .withHour(11)
                    .withMinute(30),
                endDateTime = LocalDateTime.now().withMonth(4)
                    .withDayOfMonth(6)
                    .withHour(12)
                    .withMinute(30),
                location = "Ured",
                summary = "Neki description"
            ),

            RINetEvent(
                eventId = 11,
                title = "Nakon sastanka",
                startDateTime = LocalDateTime.now().withMonth(4)
                    .withDayOfMonth(6)
                    .withHour(12)
                    .withMinute(30),
                endDateTime = LocalDateTime.now().withMonth(4)
                    .withDayOfMonth(6)
                    .withHour(13)
                    .withMinute(30),
                location = "Ured",
                summary = "Neki description"
            ),

            RINetEvent(
                eventId = 11,
                title = "Luka test",
                startDateTime = LocalDateTime.now().withMonth(4)
                    .withDayOfMonth(7)
                    .withHour(12)
                    .withMinute(30),
                endDateTime = LocalDateTime.now().withMonth(4)
                    .withDayOfMonth(7)
                    .withHour(13)
                    .withMinute(30),
                location = "Ured",
                summary = "Neki description"
            ),

            RINetEvent(
                eventId = 12,
                title = "Luka test",
                startDateTime = LocalDateTime.now().withMonth(4)
                    .withDayOfMonth(13)
                    .withHour(18)
                    .withMinute(0),
                endDateTime = LocalDateTime.now().withMonth(4)
                    .withDayOfMonth(13)
                    .withHour(18)
                    .withMinute(0),
                location = "Ured",
                summary = "Neki description"
            ),

            )
    }

    suspend fun addEvent(rinetEvent: RINetEvent): ResultOf<RINetEvent> {
        return Network.insertEvent(rinetEvent)
    }

    suspend fun updateEvent(rinetEvent: RINetEvent): ResultOf<Boolean> {
        return Network.updateEvent(rinetEvent)
    }


    suspend fun getEventWithId(eventId: Int): ResultOf<RINetEvent> =
        withContext(externalScope.coroutineContext) {


        cachedSuccessEvents.replayCache
            .last()
            .doIfSucces { latestCachedEvents ->
                latestCachedEvents.forEach {
                    if (it.eventId == eventId) return@withContext ResultOf.Success(it)
                }
            }




           return@withContext Network.getEvent(eventId)

        }

    fun makeUpdateTick() {
        tickHandler.makeTick()
    }


    suspend fun deleteEvent(eventId: Int): ResultOf<Unit> {
        return withContext(externalScope.coroutineContext) {
            Network.deleteEvent(eventId)
        }
    }

    public suspend fun makeEventsUpdate() {
        refreshEvents()
    }


}
