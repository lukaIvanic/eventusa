package com.example.eventusa.screens.addEvent.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.eventusa.extensions.PATTERN_UI_TIME
import com.example.eventusa.extensions.adjustType
import com.example.eventusa.extensions.map
import com.example.eventusa.extensions.toParsedString
import com.example.eventusa.network.Network
import com.example.eventusa.network.ResultOf
import com.example.eventusa.repository.EventsRepository
import com.example.eventusa.screens.addEvent.data.NotificationPreset
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.utils.LocalStorageManager
import com.example.eventusa.utils.notifications.MAX_MINS_UNTIL_EVENT
import com.example.eventusa.utils.notifications.NotifManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

//TODO move to proper place
data class AddEventUiState(
    var eventId: Int,
    var riNetEvent: RINetEvent,
) {
    val notificationPresets: List<NotificationPreset>
        get() {
            return LocalStorageManager.readNotifications(eventId)
                .mapNotNull { notifTimeBeforeEventMins ->
                    NotificationPreset.get(
                        notifTimeBeforeEventMins
                    )
                }
        }

    override fun equals(other: Any?): Boolean {
        return false
    }
}

const val defaultEventDuration = 60L

class AddEventViewModel(val eventsRepository: EventsRepository) : ViewModel() {

    private var currUiState =
        AddEventUiState(
            -100,
            RINetEvent(
                -1,
                null,
                LocalDateTime.now().withMinute(0).plusHours(1),
                LocalDateTime.now().withMinute(0).plusHours(2)
            )
        )

    private val _uiState =
        MutableStateFlow<ResultOf<AddEventUiState>>(ResultOf.Success(currUiState))
    val uiState = _uiState.asStateFlow()

    private val _postEventState = MutableSharedFlow<ResultOf<RINetEvent>>()
    val postEventState: SharedFlow<ResultOf<RINetEvent>> = _postEventState.asSharedFlow()

    private val _deleteEventState = MutableSharedFlow<ResultOf<Boolean>>()
    val deleteEventState = _deleteEventState.asSharedFlow()

    suspend fun updateOrInsertEvent() {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                if (currUiState.eventId > 0) {
                    val res = Network.updateEvent(currUiState.riNetEvent)
                    _postEventState.emit(res.map { currUiState.riNetEvent })
                } else {
                    val res = Network.insertEvent(currUiState.riNetEvent)
                    _postEventState.emit(res.map { currUiState.riNetEvent })
                }

                eventsRepository.makeUpdateTick()

            }

        }

    }

    suspend fun deleteEvent() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                _deleteEventState.emit(ResultOf.Loading)

                val deleteResult = Network.deleteEvent(currUiState.eventId)
                _deleteEventState.emit(deleteResult)

                eventsRepository.makeUpdateTick()

            }
        }
    }

    fun fetchEvent(eventId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val result = eventsRepository.getEventWithId(eventId)
                when (result) {
                    is ResultOf.Error, is ResultOf.Loading -> _uiState.value = result.adjustType()
                    is ResultOf.Success -> {
                        _uiState.value = result.map {
                            AddEventUiState(eventId, it)
                        }
                        currUiState = (_uiState.value as ResultOf.Success).data
                    }
                }
            }

        }
    }

    fun setNotification(context: Context, timeUntilEventMins: Long) {

        if (timeUntilEventMins > MAX_MINS_UNTIL_EVENT || timeUntilEventMins < 0) {
            _uiState.value = ResultOf.Error(Exception("Invalid notification time."))
            return
        }

        // notifsAdapter.addNotif(notificationInfo) // TODO: flow za notifikacije namjestit

        // shared preferences -> readat za eventId sve notifikacije, read trenutno stanje, edit

        // dodat u recycler, set notifikaciju s intentom, spremit u shared preferences

        currUiState.riNetEvent.apply {

            if (LocalDateTime.now() >= startDateTime) return@apply

            val timePeriodDesc =
                startDateTime.toParsedString(PATTERN_UI_TIME) + "-" +
                        endDateTime.toParsedString(PATTERN_UI_TIME)

            val eventTimeEpochMillis =
                startDateTime.atZone(ZoneId.systemDefault()).minusMinutes(timeUntilEventMins)
                    .toEpochSecond()


            NotifManager(context).scheduleExactAlarm(
                eventId,
                (title ?: "Event").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                timePeriodDesc,
                eventTimeEpochMillis,
                timeUntilEventMins.toInt()
            )


            LocalStorageManager.addNotification(eventId, timeUntilEventMins)

            _uiState.value = ResultOf.Success(currUiState.copy())
//                        NotifManager(this@AddEventActivity).scheduleExactAlarm(id, title, now.until(rinetEvent.startDateTime, ChronoUnit.SECONDS))

        }
    }

    fun deleteNotification(context: Context, timeUntilEventMins: Long) {

        // notifsAdapter.removeNotif(notificationInfo) // TODO: flow za notifikacije namjestit

        // shared preferences -> readat za eventId sve notifikacije, read trenutno stanje, edit

        // dodat u recycler, set notifikaciju s intentom, spremit u shared preferences

        currUiState.riNetEvent.apply {

            if (LocalDateTime.now() >= startDateTime) return@apply

//            val timePeriodDesc =
//                startDateTime.toParsedString(PATTERN_UI_TIME) + "-" +
//                        endDateTime.toParsedString(PATTERN_UI_TIME)
//
//            val timeFromNowToNotifSeconds = LocalDateTime.now()
//                .until(startDateTime.minusMinutes(timeUntilEventMins), ChronoUnit.SECONDS)


            NotifManager(context).deleteExactAlarm(
                eventId,
                timeUntilEventMins.toInt()
            )


            LocalStorageManager.deleteNotification(eventId, timeUntilEventMins)

            _uiState.value = ResultOf.Success(currUiState.copy())
//                        NotifManager(this@AddEventActivity).scheduleExactAlarm(id, title, now.until(rinetEvent.startDateTime, ChronoUnit.SECONDS))

        }
    }

    fun setTitle(title: String) {
        currUiState.riNetEvent.title = title
    }

    fun setLocation(location: String) {
        currUiState.riNetEvent.location = location
    }

    fun setSummary(summary: String) {
        currUiState.riNetEvent.description = summary
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
    }

    fun startTimeSet(hour: Int, minute: Int) {

        val duration = getEventDurationMinutes()

        currUiState.riNetEvent.apply {
            startDateTime = startDateTime.withHour(hour).withMinute(minute)

            if (startDateTime.toLocalDate() == endDateTime.toLocalDate())
                endDateTime = startDateTime.plusMinutes(duration)
        }

        _uiState.value = ResultOf.Success(currUiState.copy())

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