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
    }

    addToCalendarSection.setOnClickListener {
        addToCalendarCheckBox.isChecked = !addToCalendarCheckBox.isChecked
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

    addNotificationButton.setOnClickListener {
        showChooseNotificationDialog()
    }

    chooseColorSection.setOnClickListener {
        showChooseColorDialog()
    }

    deleteEventSection.setOnClickListener { handleDeleteEvent() }


}

fun AddEventActivity.showCancelEditDialog() {
    MaterialAlertDialogBuilder(this).setTitle("Cancel")
        .setMessage("Do you want to cancel your draft?\nThe information will be lost.")
        .setNegativeButton("Keep editing") { _, _ -> }.setPositiveButton("OK") { _, _ ->

            viewmodel.resetDefaultUiState()

            finishAndReturnToEventsScreen()


        }.show()
}

fun AddEventActivity.finishAndReturnToEventsScreen() {
    if (isActivityFromNotif) {
        gotoEventsScreen()
    }
    finish()
}