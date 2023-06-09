package com.example.eventusa.screens.addEvent.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.eventusa.caching.room.Room
import com.example.eventusa.caching.room.extraentities.EventNotification
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.network.ResultOf
import com.example.eventusa.notifications.NotifManager
import com.example.eventusa.repositories.EventsRepository
import com.example.eventusa.repositories.UserRepository
import com.example.eventusa.screens.addEvent.view.recycler_utils.NotificationsAdapterEvents
import com.example.eventusa.screens.events.data.EventColors
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.screens.login.model.User
import com.example.eventusa.utils.extensions.map
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

data class AddEventUiState(
    var riNetEvent: RINetEvent,
    var isDefaultEmpty: Boolean = false,
    var isCache: Boolean = false,
) {

    override fun equals(other: Any?): Boolean {
        return false
    }
}

const val defaultEventDuration = 60L

class AddEventViewModel(
    val eventsRepository: EventsRepository,
    val userRepository: UserRepository,
) : ViewModel() {

    private var currUiState = defaultUiState()

    private var originalEvent: RINetEvent? = currUiState.riNetEvent.copy()

    private val _uiState =
        MutableStateFlow<ResultOf<AddEventUiState>>(ResultOf.Success(currUiState))
    val uiState = _uiState.asStateFlow()

    private val _postEventState = MutableSharedFlow<ResultOf<RINetEvent>>()
    val postEventState: SharedFlow<ResultOf<RINetEvent>> = _postEventState.asSharedFlow()

    private val _deleteEventState = MutableSharedFlow<ResultOf<Unit>>()
    val deleteEventState = _deleteEventState.asSharedFlow()

    private val _notificationsEventState = MutableSharedFlow<ResultOf<NotificationsAdapterEvents>>()
    val notificationsEventState = _notificationsEventState.asSharedFlow()

    private val _chooseAllCheckBoxState = MutableStateFlow<Boolean>(false)
    val chooseAllCheckBoxState = _chooseAllCheckBoxState.asStateFlow()


    suspend fun updateOrInsertEvent(context: Context) {

        var s = ""

        currUiState.riNetEvent.usersAttending.dropLast(1).forEach { s += "${it.userId}, " }
        currUiState.riNetEvent.usersAttending.lastOrNull()?.let { s += it.userId }

        currUiState.riNetEvent = currUiState.riNetEvent.copy(userIdsStringList = s)

        _postEventState.emit(ResultOf.Loading)

        viewModelScope.launch(Dispatchers.IO) {

            if (currUiState.riNetEvent.eventId > 0) {

                val resultOfUpdate = eventsRepository.updateEvent(currUiState.riNetEvent)
                if (resultOfUpdate is ResultOf.Success) {
                    saveNotifications(context)
                    _postEventState.emit(ResultOf.Success(currUiState.riNetEvent))
                } else {
                    _postEventState.emit(resultOfUpdate.map { _ -> RINetEvent() })
                }

            } else {
                val insertEventResult = eventsRepository.addEvent(currUiState.riNetEvent)
                if (insertEventResult is ResultOf.Success) {
                    saveNotifications(context, insertEventResult.data.eventId)
                }
                _postEventState.emit(insertEventResult)

            }


        }

    }

    suspend fun deleteEvent(): Flow<ResultOf<Unit>> {

        viewModelScope.launch {

            val deleteEventResult = eventsRepository.deleteEvent(currUiState.riNetEvent.eventId)
            _deleteEventState.emit(deleteEventResult)


        }

        return deleteEventState
    }

    fun fetchEvent(eventId: Int) {

        viewModelScope.launch(Dispatchers.Default) {

            eventsRepository.getCachedEventWithId(eventId)?.let { cachedEvent ->
                originalEvent =
                    cachedEvent.copy(usersAttending = cachedEvent.usersAttending.toMutableList())

                emitFetchEvent(eventId, ResultOf.Success(cachedEvent))
            }


            val eventResultOfDeferred = async {
                eventsRepository.getEventWithId(eventId)
            }

            launch {
                delay(1000) // Wait time before showing loading bar
                if (!eventResultOfDeferred.isCompleted) {
                    _uiState.emit(ResultOf.Loading)
                }
            }

            val eventResultOf = eventResultOfDeferred.await()

            if (eventResultOf is ResultOf.Success) {
                originalEvent =
                    eventResultOf.data.copy(usersAttending = eventResultOf.data.usersAttending.toMutableList())
                fetchNotifications()
            }

            emitFetchEvent(eventId, eventResultOf)
        }





    }

    private fun fetchNotifications(){
        viewModelScope.launch {
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

    private suspend fun emitFetchEvent(
        eventId: Int,
        result: ResultOf<RINetEvent>,
    ) {
        if (result is ResultOf.Error) {
            _uiState.emit(result)
            return
        }

        _uiState.value = result.map {
            AddEventUiState(it.copy(eventId = eventId))
        }
        currUiState = (_uiState.value as ResultOf.Success).data.copy()

    }

    fun getAttendingUsers(): MutableList<User> {

        val exHandler = CoroutineExceptionHandler(handler = {context, throwable ->
            print(throwable.message)
        })

        viewModelScope.launch{
            launch{

                try{
                    throw RuntimeException()
                }catch(e: Exception){

                }


            }


        }



        return currUiState.riNetEvent.usersAttending
    }

    private val addedNotifications = mutableListOf<EventNotification>()
    private val deletedNotifications = mutableListOf<EventNotification>()
    private var changedStartDateTime: LocalDateTime? = null

    suspend fun addNotification(minsUntilEvent: Int): Boolean = withContext(NonCancellable) {

        val eventNotification = EventNotification(currUiState.riNetEvent.eventId, minsUntilEvent)


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

        if (addedNotifications.firstOrNull { it.minutesBeforeEvent == eventNotification.minutesBeforeEvent } != null) {
            addedNotifications.remove(eventNotification) // notification wasnt set yet, but added and removed in the activity
        } else {
            deletedNotifications.add(eventNotification) // notification already set, so need to remove it
        }

    }

    fun deleteEventNotifications(context: Context, notifs: List<EventNotification>){
        deletedNotifications.addAll(notifs)
        saveNotifications(context)
    }

    private fun saveNotifications(context: Context, eventId: Int = currUiState.riNetEvent.eventId) {
        CoroutineScope(NonCancellable).launch {


            deletedNotifications.map {
                var eventNotif = it.copy(eventId = eventId)
                eventNotif.notifId = it.notifId
                return@map eventNotif
            }.forEach { eventNotification ->
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
                updateNotifications(context, eventId)
            }

            addedNotifications.map {
                return@map it.copy(eventId = eventId)
            }.forEach { eventNotification ->
                val success = NotifManager(context).createOrUpdateEventNotif(
                    currUiState.riNetEvent.copy(eventId = eventId),
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

    suspend fun updateNotifications(
        context: Context,
        eventId: Int = currUiState.riNetEvent.eventId,
    ) {
        val eventNotifications = Room.getEventNotifications(eventId)
        eventNotifications.forEach {
            NotifManager(context).createOrUpdateEventNotif(
                currUiState.riNetEvent, it.minutesBeforeEvent, it.notifId
            )
        }

    }

    fun onActivityFinish() {
        addedNotifications.clear()
        deletedNotifications.clear()
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

    fun setCalendarEnabled(calendarEnabled: Boolean) {
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
        return currUiState.riNetEvent.usersAttending.containsAll(UserRepository.getAllUsers())
    }

    /**
     * Check if every user is highlighted, if not, then add all users to event.
     * If all the users were already checked, remove all users from event.
     * @return true if all user chips are highlighted, false if all user chips are deactivated.
     */
    fun selectAllUserChips(isAllSelected: Boolean) {

        currUiState.riNetEvent.usersAttending.apply {

            if (isAllSelected) {
                clear()
                addAll(UserRepository.getAllUsers())
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

            if (startDateTime.toLocalDate() == endDateTime.toLocalDate()) endDateTime =
                startDateTime.plusMinutes(duration)
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

        adjustDateTime(getEventDurationMinutes())

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
        RINetEvent(
            -1,
            "",
            LocalDateTime.now().withMinute(0).plusHours(1),
            LocalDateTime.now().withMinute(0).plusHours(2),
            usersAttending = ArrayList(),
            eventColor = if (LocalStorageManager.readRandomColorWhenCreatingEvent()) EventColors.getRandomColor() else EventColors.RINET_BLUE
        ), isDefaultEmpty = true
    )

    fun wasEditMade(): Boolean {
        val first = currUiState.riNetEvent
        val second = originalEvent
        return first != second || addedNotifications.isNotEmpty()
    }

}

class AddEventViewModelFactory(
    private val eventsRepository: EventsRepository,
    private val userRepository: UserRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return AddEventViewModel(
                eventsRepository,
                userRepository
            ) as T
        }
        throw java.lang.IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
    }

}