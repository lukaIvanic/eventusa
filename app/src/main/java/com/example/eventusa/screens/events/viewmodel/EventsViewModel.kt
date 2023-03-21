package com.example.eventusa.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eventusa.extensions.map
import com.example.eventusa.network.ResultOf
import com.example.eventusa.repository.EventsRepository
import com.example.eventusa.repository.UserRepository
import com.example.eventusa.screens.events.data.EventItem
import com.example.eventusa.utils.DataUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
        return  userRepository.user?.Name ?: "RI-NET employee!"
    }


    private fun fetchEvents() {

        viewModelScope.launch {

            eventsRepository.currentEventsResult
                .collectLatest { resultOf ->

                    _eventsUiState.value = resultOf.map { rinetEvents ->
                        EventsUiState(DataUtils.eventsDisplayItems(rinetEvents))
                    }
                }

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