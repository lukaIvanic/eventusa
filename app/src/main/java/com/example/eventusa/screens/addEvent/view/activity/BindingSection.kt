package com.example.eventusa.screens.addEvent.view.activity

import com.example.eventusa.R

fun AddEventActivity.bindViews(){

    addEventActivityLayout = findViewById(R.id.addEventActivityLayout)

    titleEditText = findViewById(R.id.addTitleEditText)

    saveEventButton = findViewById(R.id.saveEventButton)
    saveEventButton = findViewById(R.id.saveEventButton)
    cancelButton = findViewById(R.id.cancelNotifRowButton)

    addToCalendarSection = findViewById(R.id.addToCalendarSection)
    addToCalendarCheckBox = findViewById(R.id.addToCalendarCheckBox)

    startDateTextView = findViewById(R.id.dateStartTextView)
    startTimeTextView = findViewById(R.id.timeStartTextView)
    endDateTextView = findViewById(R.id.dateEndTextView)
    endTimeTextView = findViewById(R.id.timeEndTextView)

    chooseAllSection = findViewById(R.id.chooseAllSection)
    chooseAllSwitch = findViewById(R.id.chooseAllSwitch)
    peopleChipGroup = findViewById(R.id.peoplChipGroup)


    locationEditText = findViewById(R.id.locationEditText)
    summaryEditText = findViewById(R.id.summaryEditText)

    addNotificationButton = findViewById(R.id.addNotificationButton)
    notifsRecyclerView = findViewById(R.id.notificationsRecyclerView)


    chooseColorSection = findViewById(R.id.chooseColorSection)
    chooseColorCircle = findViewById(R.id.chooseColorCircle)
    eventColorTextView = findViewById(R.id.eventColorTextView)

    deleteEventSection = findViewById(R.id.deleteEventSection)
    deleteEventSectionDivider = findViewById(R.id.deleteEventSectionDivider)
}