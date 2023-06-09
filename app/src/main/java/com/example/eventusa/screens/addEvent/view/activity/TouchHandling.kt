package com.example.eventusa.screens.addEvent.view.activity

import com.example.eventusa.utils.animateChange
import com.google.android.material.dialog.MaterialAlertDialogBuilder


fun AddEventActivity.setupTouch() {

    cancelButton.setOnClickListener {

        sendInputsToViewmodel()

        if (viewmodel.wasEditMade()) {
            showCancelEditDialog()
        } else {
            finishAndReturnToEventsScreen()
        }

    }

    saveEventButton.setOnClickListener {
        saveEventButton.animateChange(
            200, fromScaleX = 1F, toScaleX = 1.2F, fromScaleY = 1F, toScaleY = 0.7F
        )
        handleSaveEvent()
        titleEditText.clearFocus()
    }

    addToCalendarSection.setOnClickListener {
        addToCalendarCheckBox.isChecked = !addToCalendarCheckBox.isChecked
        viewmodel.setCalendarEnabled(addToCalendarCheckBox.isChecked)
    }

    startDateTextView.setOnClickListener { setupStartDatePicker() }
    endDateTextView.setOnClickListener { setupEndDatePicker() }

    startTimeTextView.setOnClickListener { setupStartTimePicker() }
    endTimeTextView.setOnClickListener { setupEndTimePicker() }


    chooseAllSection.setOnClickListener {
        chooseAllSwitch.isChecked = !chooseAllSwitch.isChecked
        handleChooseAllSwitch()
    }

    chooseAllSwitch.setOnClickListener { handleChooseAllSwitch() }

    locationSectionLayout.setOnClickListener {
        openLocationPickDialog()
    }

    addNotificationButton.setOnClickListener {
        showChooseNotificationDialog()
    }

    chooseColorSection.setOnClickListener {
        showChooseColorDialog()
    }

    deleteEventSection.setOnClickListener {
        showDeleteEventDialog()
    }


}

fun AddEventActivity.showCancelEditDialog() {
    MaterialAlertDialogBuilder(this).setTitle("Cancel")
        .setMessage("Do you want to cancel your draft?\nThe information will be lost.")
        .setNegativeButton("Keep editing") { _, _ -> }.setPositiveButton("YES") { _, _ ->

            viewmodel.resetDefaultUiState()

            finishAndReturnToEventsScreen()


        }.show()
}

fun AddEventActivity.showDeleteEventDialog() {
    MaterialAlertDialogBuilder(this).setTitle("Delete")
        .setMessage("Do you really want to delete this event?\nThis action cannot be reverted.")
        .setNegativeButton("Cancel") { _, _ -> }.setPositiveButton("DELETE") { _, _ ->
            handleDeleteEvent()
        }.show()
}

fun AddEventActivity.finishAndReturnToEventsScreen() {
    viewmodel.onActivityFinish()
    if (isActivityFromNotif) {
        gotoEventsScreen()
    }
    finish()
}