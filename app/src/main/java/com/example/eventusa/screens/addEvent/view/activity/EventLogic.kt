package com.example.eventusa.screens.addEvent.view.activity

import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.eventusa.network.ResultOf
import com.example.eventusa.utils.extensions.doIfFailure
import com.example.eventusa.utils.extensions.doIfLoading
import com.example.eventusa.utils.extensions.doIfSucces
import com.example.eventusa.utils.extensions.toParsedString
import com.example.eventusa.utils.setChipHighlighted
import com.example.eventusa.utils.setTextAnimated
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

fun AddEventActivity.getIntentEventId(): Int? {
    val eventId = intent.getIntExtra("event_id", -550)
    return if (eventId != -550) eventId else null
}

fun AddEventActivity.handleSaveEvent() {

    showProgressDialog("Saving event..")

    lifecycleScope.launch {
        sendInputsToViewmodel()
        viewmodel.updateOrInsertEvent(this@handleSaveEvent)
    }
}


fun AddEventActivity.handleDeleteEvent() {
    lifecycleScope.launch {

        showProgressDialog("Deleting event..")

        viewmodel.deleteEvent().stateIn(this).collect { result ->

            if (result !is ResultOf.Loading) {
                hideProgressDialog()
            }


            result.doIfFailure {
                showToast(it.localizedMessage)
            }

            result.doIfSucces {

                viewmodel.deleteEventNotifications(this@handleDeleteEvent, notifsAdapter.getNotifs())

                finish()
            }

        }

    }
}

fun AddEventActivity.sendInputsToViewmodel() {
    viewmodel.setTitle(titleEditText.text.toString())
    viewmodel.setLocation(locationEditText.text.toString())
    viewmodel.setSummary(summaryEditText.text.toString())
    viewmodel.setCalendarEnabled(addToCalendarCheckBox.isChecked)
}


fun AddEventActivity.setupSaveEventFlowObserving() {
    lifecycleScope.launch {
        viewmodel.postEventState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { result ->
                if (result is ResultOf.Loading) {

                    showProgressDialog(if (isActivityEditEvent) "Saving event.." else "Creating event..")
                } else {
                    hideProgressDialog()
                }

                result.doIfFailure {
                    showToast(it.localizedMessage)
                }

                result.doIfSucces {
                    finish()
                }
            }
    }
}

fun AddEventActivity.setupFetchEventStateObserving() {
    lifecycleScope.launch {

        viewmodel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { result ->

                result.doIfLoading {
                    showProgressDialog("Fetching event..")
                }

                result.doIfFailure {
                    hideProgressDialog()
                    Toast.makeText(
                        this@setupFetchEventStateObserving, it.localizedMessage, Toast.LENGTH_LONG
                    ).show()
                }

                result.doIfSucces { state ->

                    if (!state.isDefaultEmpty) {
                        hideProgressDialog()
                    }


                    state.riNetEvent.apply {

                        if (isActivityEditEvent) {
                            titleEditText.setText(title)
                            titleEditText.clearFocus()
                        }

                        if(titleEditText.text.isNotEmpty()){
                            titleEditText.clearFocus()
                        }

                        addToCalendarCheckBox.isChecked = isInCalendar

                        startDateTextView.setTextAnimated(
                            startDateTime.toLocalDate().toParsedString()
                        )
                        startTimeTextView.setTextAnimated(
                            startDateTime.toLocalTime().toParsedString()
                        )
                        endDateTextView.setTextAnimated(
                            endDateTime.toLocalDate().toParsedString()
                        )
                        endTimeTextView.setTextAnimated(
                            endDateTime.toLocalTime().toParsedString()
                        )

                        if (locationEditText.text.isEmpty()) locationEditText.setText(location)

                        if (summaryEditText.text.isEmpty()) summaryEditText.setText(summary)


                        var allChipsHighlighted = true

                        peopleChipGroup.children.iterator().withIndex().forEach { indexedChip ->
                            val chipTv = indexedChip.value as? TextView ?: return@forEach
                            val user =
                                usersAttending.firstOrNull { it.displayName == chipTv.text.toString() }

                            if (user != null) {
                                chipTv.setChipHighlighted(user.displayName)

                                // Animation eye sugar
                                val delayTime = 100 / (indexedChip.index / 2).plus(1)
                                delay(delayTime.toLong())
                            } else {
                                allChipsHighlighted = false
                            }

                        }

                        chooseAllSwitch.isChecked = allChipsHighlighted

                        updateEventCircleColor(eventColor)


                    }
                }

            }
    }
}
fun AddEventActivity.showToast(message: String?) {

    Toast.makeText(this, message ?: "An error occured.", Toast.LENGTH_SHORT).show()

}