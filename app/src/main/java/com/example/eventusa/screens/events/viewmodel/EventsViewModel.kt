package com.example.eventusa.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eventusa.network.ResultOf
import com.example.eventusa.repositories.EventsRepository
import com.example.eventusa.repositories.UserRepository
import com.example.eventusa.screens.events.data.EventItem
import com.example.eventusa.utils.DataUtils
import com.example.eventusa.utils.extensions.map
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventsUiState(
    val eventsItemsList: MutableList<EventItem> = ArrayList(),
)


class EventsViewModel(
    private val eventsRepository: EventsRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _eventsUiState: MutableStateFlow<ResultOf<EventsUiState>> =
        MutableStateFlow(ResultOf.Loading)
    val eventsUiState = _eventsUiState.asStateFlow()


    init {
        fetchEvents()
    }

    fun getUsername(): String {
        return userRepository.user?.name ?: "RI-NET employee!"
    }


    private fun fetchEvents() {

        viewModelScope.launch {


            launch {
                eventsRepository.currentEventsResult
                    .collect { resultOf ->

                        _eventsUiState.value = resultOf.map { rinetEvents ->
                            EventsUiState(DataUtils.eventsDisplayItems(rinetEvents))
                        }
                    }
            }

            delay(100)
            eventsRepository.makeEventsUpdate()


        }

    }

    fun makeEventsUpdateTick(){
        viewModelScope.launch {
            eventsRepository.makeEventsUpdate()
        }
    }

}


class EventsViewModelFactory(
    private val eventsRepository: EventsRepository,
    private val userRepository: UserRepository,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventsViewModel(eventsRepository, userRepository) as T
        }
        throw java.lang.IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
    }

}