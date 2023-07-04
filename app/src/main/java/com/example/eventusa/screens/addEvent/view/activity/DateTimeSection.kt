package com.example.eventusa.screens.addEvent.view.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import java.time.LocalDate
import java.util.*

//Start Date

fun AddEventActivity.handleSetStartDate(year: Int, month: Int, day: Int) {
    viewmodel.startDateSet(year, month, day)
}

fun AddEventActivity.setupStartDatePicker() {

    val onDateSetListener = DatePickerDialog.OnDateSetListener {
            datePicker: DatePicker?,
            year: Int,
            month: Int,
            day: Int,
        ->

        val startDate = LocalDate.now().withYear(year)
            .withMonth(month + 1) // Calendar month starts from 0, Local date month starts from 1, we need to adjust
            .withDayOfMonth(day)

        if (LocalStorageManager.readAskConfirmDateBefore() && startDate < LocalDate.now()) {
            confirmDateBeforeTodayDialog(year, month, day)
        } else {
            handleSetStartDate(year, month, day)
        }


    }



    val startDate = viewmodel.getCurrStartDateTime()
    startDate.month

    DatePickerDialog(
        this@setupStartDatePicker,
        onDateSetListener,
        startDate.year,
        startDate.monthValue - 1,
        startDate.dayOfMonth
    ).apply {
        datePicker.firstDayOfWeek = Calendar.MONDAY
        setCanceledOnTouchOutside(false)
        show()
    }
}


//End Date

fun AddEventActivity.setupEndDatePicker() {

    val onDateSetListener = DatePickerDialog.OnDateSetListener {
            datePicker: DatePicker?,
            year: Int,
            month: Int,
            day: Int,
        ->
        viewmodel.endDateSet(year, month, day)
    }

    val endDate = viewmodel.getCurrEndDateTime()

    DatePickerDialog(
        this@setupEndDatePicker,
        onDateSetListener,
        endDate.year,
        endDate.monthValue - 1,
        endDate.dayOfMonth
    ).apply {
        datePicker.firstDayOfWeek = Calendar.MONDAY
        setCanceledOnTouchOutside(false)
        show()
    }
}


// Start Time

fun AddEventActivity.setupStartTimePicker() {
    val onTimeSetListener = TimePickerDialog.OnTimeSetListener {
            timePicker: TimePicker?,
            hour: Int,
            minute: Int,
        ->
        viewmodel.startTimeSet(hour, minute)
    }

    val startTime = viewmodel.getCurrStartDateTime()

    TimePickerDialog(
        this@setupStartTimePicker, onTimeSetListener, startTime.hour, startTime.minute, true
    ).apply {

        setCanceledOnTouchOutside(false)
        show()
    }
}

fun AddEventActivity.setupEndTimePicker() {
    val onTimeSetListener = TimePickerDialog.OnTimeSetListener {
            timePicker: TimePicker?,
            hour: Int,
            minute: Int,
        ->

        viewmodel.endTimeSet(hour, minute)

    }

    val endTime = viewmodel.getCurrEndDateTime()

    TimePickerDialog(
        this@setupEndTimePicker, onTimeSetListener, endTime.hour, endTime.minute, true
    ).apply {
        setCanceledOnTouchOutside(false)
        show()
    }
}

fun AddEventActivity.confirmDateBeforeTodayDialog(year: Int, month: Int, day: Int) {

    // initialise the alert dialog builder
    AlertDialog.Builder(this)
        .setTitle("Date before today")
        .setMessage("The starting date is before today's date. Are you sure you want to proceed?")
        .setPositiveButton("Yes") { _, _ ->
            handleSetStartDate(year, month, day)
        }.setNegativeButton("No") { _, _ -> }
        .setNeutralButton("Don't ask again") { _, _ ->
            LocalStorageManager.setUncheckedAskConfirmDateBefore()
            handleSetStartDate(year, month, day)
        }
        .create()
        .show()
}

