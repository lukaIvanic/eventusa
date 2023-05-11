package com.example.eventusa.screens.addEvent.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.eventusa.caching.room.Room
import com.example.eventusa.caching.room.extraentities.EventNotification
import com.example.eventusa.network.ResultOf
import com.example.eventusa.notifications.NotifManager
import com.example.eventusa.repositories.EventsRepository
import com.example.eventusa.screens.addEvent.view.recycler_utils.NotificationsAdapterEvents
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.screens.login.model.User
import com.example.eventusa.utils.extensions.map
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

//TODO move to proper place
data class AddEventUiState(
    var eventId: Int,
    var riNetEvent: RINetEvent,
) {

    override fun equals(other: Any?): Boolean {
        return false
    }
}

const val defaultEventDuration = 60L

class AddEventViewModel(val eventsRepository: EventsRepository) : ViewModel() {

    private var currUiState =
        defaultUiState()

    private var originalEvent: RINetEvent? = null

    private val _uiState =
        MutableStateFlow<ResultOf<AddEventUiState>>(ResultOf.Success(currUiState))
    val uiState = _uiState.asStateFlow()

    private val _postEventState = MutableSharedFlow<ResultOf<RINetEvent>>()
    val postEventState: SharedFlow<ResultOf<RINetEvent>> = _postEventState.asSharedFlow()

    private val _deleteEventState = MutableSharedFlow<ResultOf<Boolean>>()
    val deleteEventState = _deleteEventState.asSharedFlow()

    private val _notificationsEventState = MutableSharedFlow<ResultOf<NotificationsAdapterEvents>>()
    val notificationsEventState = _notificationsEventState.asSharedFlow()

    private val _chooseAllCheckBoxState = MutableStateFlow<Boolean>(false)
    val chooseAllCheckBoxState = _chooseAllCheckBoxState.asStateFlow()


    suspend fun updateOrInsertEvent(context: Context) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {

//                if (currUiState.eventId > 0) {
//                    val res = Network.updateEvent(currUiState.riNetEvent)
//                    _postEventState.emit(res.map { currUiState.riNetEvent })
//                } else {
//                    val res = Network.insertEvent(currUiState.riNetEvent)
//                    _postEventState.emit(res.map { currUiState.riNetEvent })
//                }

                if (currUiState.eventId > 0) {

                    eventsRepository.updateEvent(currUiState.riNetEvent)
                    saveNotifications(context)
                    _postEventState.emit(ResultOf.Success(currUiState.riNetEvent))
                } else {
                    eventsRepository.addEvent(currUiState.riNetEvent)
                    saveNotifications(context)
                    _postEventState.emit(ResultOf.Success(currUiState.riNetEvent))
                }

                eventsRepository.makeUpdateTick()

            }

        }

    }

    suspend fun deleteEvent() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                _deleteEventState.emit(ResultOf.Loading)
//
//                val deleteResult = Network.deleteEvent(currUiState.eventId)
//                _deleteEventState.emit(deleteResult)

                eventsRepository.deleteEvent(currUiState.eventId)
                _deleteEventState.emit(ResultOf.Success(true))
                eventsRepository.makeUpdateTick()

            }
        }
    }

    fun fetchEvent(eventId: Int) {

        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.i("Fetch event - Add Event View model", throwable.localizedMessage)
        }

        viewModelScope.launch(coroutineExceptionHandler) {
            withContext(Dispatchers.Default) {

                val deferred = async {
                    val result = eventsRepository.getEventWithId(eventId)
                    if (result is ResultOf.Error) {
                        return@async null
                    }
                    return@async result
                }

                val cachedEventResult = deferred.await()

                if (cachedEventResult != null) {

                    if(cachedEventResult is ResultOf.Success){
                        originalEvent = cachedEventResult.data.copy(usersAttending = cachedEventResult.data.usersAttending?.toMutableList())
                    }

                    emitFetchEvent(eventId, cachedEventResult)
                    return@withContext
                }

                try {
                    val eventFromDb = eventsRepository.getEventWithId(eventId)
                    emitFetchEvent(eventId, eventFromDb)

                    if(eventFromDb is ResultOf.Success){
                        originalEvent = eventFromDb.data.copy(usersAttending = eventFromDb.data.usersAttending?.toMutableList())
                    }else if (eventFromDb is ResultOf.Error){
                        throw eventFromDb.exception
                    }
                } catch (e: Exception) {
                    emitFetchEvent(eventId, ResultOf.Error(Exception("Event was deleted.")))
                }


            }

            /*
                 _uiState.value = result.map {
                            AddEventUiState(eventId, it.copy())
                        }

                        originalEvent = result.data.copy()

                        currUiState = (_uiState.value as ResultOf.Success).data
             */

            _notificationsEventState.emit(
                ResultOf.Success(
                    NotificationsAdapterEvents.LOAD_EVENTS(
                        Room.getEventNotifications(
                            currUiState.riNetEvent.eventId
                        )
                    )
                )
            )
        }


    }

    private fun emitFetchEvent(eventId: Int, result: ResultOf<RINetEvent>) {

        _uiState.value = result.map {
            AddEventUiState(eventId, it.copy())
        }
        currUiState = (_uiState.value as ResultOf.Success).data

    }

    private val addedNotifications = mutableListOf<EventNotification>()
    private val deletedNotifications = mutableListOf<EventNotification>()
    private var changedStartDateTime: LocalDateTime? = null

    suspend fun addNotification(minsUntilEvent: Int): Boolean =
        withContext(NonCancellable) {

            val eventNotification =
                EventNotification(currUiState.riNetEvent.eventId, minsUntilEvent)


            if (eventNotification.minutesBeforeEvent < 0) {
                _notificationsEventState.emit(ResultOf.Error(Exception("Invalid notification time.")))
                return@withContext false
            }


            if (LocalDateTime.now() >= currUiState.riNetEvent.startDateTime) {
                _notificationsEventState.emit(ResultOf.Error(java.lang.Exception("Can't set notifications for an event that already started.")))
                return@withContext false
            }

            addedNotifications.add(eventNotification)
            return@withContext true
        }

    fun removeNotification(eventNotification: EventNotification) {

        deletedNotifications.add(eventNotification)

    }

    fun saveNotifications(context: Context) {
        CoroutineScope(NonCancellable).launch {

            deletedNotifications.forEach { eventNotification ->
                NotifManager(context).deleteEventNotification(
                    eventNotification.notifId,
                )

                _notificationsEventState.emit(
                    ResultOf.Success(
                        NotificationsAdapterEvents.DELETE_EVENT(
                            eventNotification
                        )
                    )
                )
            }

            changedStartDateTime?.let {
                updateNotifications(context)
            }

            addedNotifications.forEach { eventNotification ->
                val success = NotifManager(context).createOrUpdateEventNotif(
                    currUiState.riNetEvent,
                    eventNotification.minutesBeforeEvent
                )

                if (!success) {
                    _notificationsEventState.emit(ResultOf.Error(Exception("Couldn't create notification")))
                    return@launch
                }


                _notificationsEventState.emit(
                    ResultOf.Success(
                        NotificationsAdapterEvents.ADD_EVENT(
                            eventNotification
                        )
                    )
                )
            }
        }
    }

    suspend fun updateNotifications(context: Context) {
        val eventNotifications = Room.getEventNotifications(currUiState.riNetEvent.eventId)
        eventNotifications.forEach {
            NotifManager(context).createOrUpdateEventNotif(
                currUiState.riNetEvent,
                it.minutesBeforeEvent,
                it.notifId
            )
        }

    }

    fun setTitle(title: String) {
        currUiState.riNetEvent.title = title
    }

    fun setLocation(location: String) {
        currUiState.riNetEvent.location = location
    }

    fun setSummary(summary: String) {
        currUiState.riNetEvent.summary = summary
    }

    fun setCalendarEnabled(calendarEnabled: Boolean){
        currUiState.riNetEvent.isInCalendar = calendarEnabled
    }

    /**
     * @return false if new chip state is default, true if new chip state is highlighted
     */
    fun userChipClicked(user: User): Boolean {

        currUiState.riNetEvent.usersAttending?.apply {

            val isHighlightedState = contains(user)

            if (isHighlightedState) {
                remove(user)
            } else {
                add(user)
            }

            _chooseAllCheckBoxState.value = isAllUserChipsActivated()

            return contains(user)

        }

        return false

    }

    fun isAllUserChipsActivated(): Boolean {
        return currUiState.riNetEvent.usersAttending?.containsAll(User.getAllUsers()) ?: false
    }

    /**
     * Check if every user is highlighted, if not, then add all users to event.
     * If all the users were already checked, remove all users from event.
     * @return true if all user chips are highlighted, false if all user chips are deactivated.
     */
    fun selectAllUserChips(isAllSelected: Boolean) {

        currUiState.riNetEvent.usersAttending?.apply {

            if (isAllSelected) {
                clear()
                addAll(User.getAllUsers())
            } else {
                clear()
            }

        }

    }

    fun startDateSet(year: Int, month: Int, day: Int) {

        val durationDays = getEventDurationDays()
        val durationMinutes = getEventDurationMinutes()

        currUiState.riNetEvent.apply {
            startDateTime = startDateTime.withYear(year).withMonth(month + 1).withDayOfMonth(day)
            endDateTime = startDateTime.plusDays(durationDays).plusMinutes(durationMinutes)
        }

        adjustDateTime(eventDurationDays = durationDays)



        _uiState.value = ResultOf.Success(currUiState.copy())

        changedStartDateTime = currUiState.riNetEvent.startDateTime

    }

    fun startTimeSet(hour: Int, minute: Int) {

        val duration = getEventDurationMinutes()

        currUiState.riNetEvent.apply {
            startDateTime = startDateTime.withHour(hour).withMinute(minute)

            if (startDateTime.toLocalDate() == endDateTime.toLocalDate())
                endDateTime = startDateTime.plusMinutes(duration)
        }

        _uiState.value = ResultOf.Success(currUiState.copy())


        changedStartDateTime = currUiState.riNetEvent.startDateTime
    }


    fun endDateSet(year: Int, month: Int, day: Int) {

        val currentEventDuration = getEventDurationMinutes()

        currUiState.riNetEvent.apply {
            endDateTime = endDateTime.withYear(year).withMonth(month + 1).withDayOfMonth(day)
        }

        adjustDateTime(currentEventDuration)

        _uiState.value = ResultOf.Success(currUiState.copy())
    }

    fun endTimeSet(hour: Int, minute: Int) {

        currUiState.riNetEvent.apply {
            endDateTime = endDateTime.withHour(hour).withMinute(minute)

        }

        _uiState.value = ResultOf.Success(currUiState.copy())

    }

    private fun adjustDateTime(
        eventDurationMins: Long = defaultEventDuration,
        eventDurationDays: Long = 0,
    ) {

        currUiState.riNetEvent.apply {
            if (endDateTime.toLocalDate() <= startDateTime.toLocalDate()) {
                endDateTime =
                    startDateTime.plusDays(eventDurationDays).plusMinutes(eventDurationMins)

                if (endDateTime.toLocalTime() < startDateTime.toLocalTime()) {
                    endDateTime = startDateTime.plusHours(eventDurationMins / 60)
                        .plusMinutes(eventDurationMins % 60)

                }
            }
        }
    }


    private fun getEventDurationDays(): Long {
        return currUiState.riNetEvent.startDateTime.toLocalDate()
            .until(currUiState.riNetEvent.endDateTime.toLocalDate(), ChronoUnit.DAYS)
    }

    private fun getEventDurationMinutes(): Long {
        val duration = currUiState.riNetEvent.startDateTime.toLocalTime()
            .until(currUiState.riNetEvent.endDateTime.toLocalTime(), ChronoUnit.MINUTES)

        return if (duration > 0) duration else defaultEventDuration
    }


    fun getCurrStartDateTime(): LocalDateTime {
        return currUiState.riNetEvent.startDateTime
    }

    fun getCurrEndDateTime(): LocalDateTime {
        return currUiState.riNetEvent.endDateTime
    }

    fun setEventColor(eventColor: Int) {
        currUiState.riNetEvent.eventColor = eventColor
    }

    fun getEventColor(): Int {
        return currUiState.riNetEvent.eventColor
    }


    fun resetDefaultUiState() {
        currUiState = defaultUiState()
        _uiState.value = ResultOf.Success(currUiState.copy())
        _chooseAllCheckBoxState.value = isAllUserChipsActivated()
    }

    fun defaultUiState() = AddEventUiState(
        -100,
        RINetEvent(
            -1,
            "",
            LocalDateTime.now().withMinute(0).plusHours(1),
            LocalDateTime.now().withMinute(0).plusHours(2),
            usersAttending = ArrayList()
        )
    )

    fun wasEditMade(): Boolean {
        val first = currUiState.riNetEvent
        val second = originalEvent
        return first != second || addedNotifications.isNotEmpty()
    }

}

class AddEventViewModelFactory(private val eventsRepository: EventsRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEventViewModel(eventsRepository) as T
        }
        throw java.lang.IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
    }

}