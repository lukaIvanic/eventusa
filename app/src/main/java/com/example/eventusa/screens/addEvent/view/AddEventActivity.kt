package com.example.eventusa.screens.addEvent.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.util.TypedValue
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.eventusa.R
import com.example.eventusa.app.EventusaApplication
import com.example.eventusa.extensions.*
import com.example.eventusa.screens.addEvent.viewmodel.AddEventViewModel
import com.example.eventusa.screens.addEvent.viewmodel.AddEventViewModelFactory
import com.example.eventusa.utils.adaptStyleToTag
import com.example.eventusa.utils.animateChange
import com.example.eventusa.utils.setChipDefault
import com.example.eventusa.utils.setTextAnimated
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AddEventActivity : AppCompatActivity() {

    val viewmodel: AddEventViewModel by viewModels { AddEventViewModelFactory((application as EventusaApplication).eventsRepository) }

    lateinit var progressDialog: AlertDialog

    lateinit var saveEventButton: TextView
    lateinit var cancelButton: ImageView

    lateinit var titleEditText: EditText

    lateinit var startDateTextView: TextView
    lateinit var startTimeTextView: TextView
    lateinit var endDateTextView: TextView
    lateinit var endTimeTextView: TextView

    lateinit var chooseAllSection: ConstraintLayout
    lateinit var chooseAllSwitch: Switch
    lateinit var peopleChipGroup: ChipGroup

    lateinit var locationEditText: EditText
    lateinit var summaryEditText: EditText

    lateinit var addToCalendarSection: LinearLayout
    lateinit var addToCalendarCheckBox: CheckBox

    lateinit var deleteEventSection: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        titleEditText = findViewById(R.id.addTitleEditText)

        saveEventButton = findViewById(R.id.saveEventButton)
        cancelButton = findViewById(R.id.cancelEventButton)

        startDateTextView = findViewById(R.id.dateStartTextView)
        startTimeTextView = findViewById(R.id.timeStartTextView)
        endDateTextView = findViewById(R.id.dateEndTextView)
        endTimeTextView = findViewById(R.id.timeEndTextView)

        chooseAllSection = findViewById(R.id.chooseAllSection)
        chooseAllSwitch = findViewById(R.id.chooseAllSwitch)
        peopleChipGroup = findViewById(R.id.peoplChipGroup)


        locationEditText = findViewById(R.id.locationEditText)
        summaryEditText = findViewById(R.id.summaryEditText)

        addToCalendarSection = findViewById(R.id.addToCalendarSection)
        addToCalendarCheckBox = findViewById(R.id.addToCalendarCheckBox)

        deleteEventSection = findViewById(R.id.deleteEventSection)

        setupUI()
        setupStateObserving()

    }

    private fun setupStateObserving() {

        val eventId = getIntentEventId()
        eventId?.let {
            viewmodel.fetchEvent(eventId)
        }

        lifecycleScope.launch {
            viewmodel.uiState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { result ->
                    result.doIfLoading {
                        Toast.makeText(
                            this@AddEventActivity,
                            "Loading event..",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    result.doIfFailure {
                        Toast.makeText(
                            this@AddEventActivity,
                            it.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    result.doIfSucces { state ->

                        state.riNetEvent.apply {

                            if (titleEditText.text.isEmpty() && !title.isNullOrEmpty()) {
                                titleEditText.setText(title)
                                titleEditText.clearFocus()
                            }

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

                            if (locationEditText.text.isEmpty())
                                locationEditText.setText(location)

                            if (summaryEditText.text.isEmpty())
                                summaryEditText.setText(description)

                        }
                    }

                }
        }


    }

    private fun setupUI() {
        setupPeopleChips()
        setupProgressDialog()
        setupTouch()
    }

    private fun setupPeopleChips() {
        val names = arrayListOf(
            "Luka Ivanic",
            "Anja Stefan",
            "Armando Sćulac",
            "Branko Kojić",
            "Branko Zuza",
            "Danijel Pajalic",
            "Draško Andrić",
            "Nevija",
            "Zvjezdana",
            "David Pajo",
            "Marko Andrić",
            "Ivo Opancar"
        )

        names.forEach {
            addRinetChip(it)
        }

    }

    private fun addRinetChip(name: String) {
        val textView = TextView(this)

        peopleChipGroup.addView(textView)

        textView.apply {
            tag = "default"
            setChipDefault(name, animate = false)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
            setPadding(dpToPx(10F), dpToPx(8F), dpToPx(10F), dpToPx(8F))


            setOnClickListener {

                tag = if (tag == "highlight") "default" else "highlight"

                adaptStyleToTag(name)

                val areDefaults = areAllChipsDefault()
                chooseAllSwitch.isChecked = !(areDefaults == null || areDefaults == true)

            }
        }

    }

    // true -> every chip is not selected
    // false -> every chip is selected
    // null -> mixed states
    // todo: prebacit u viewmodel
    private fun areAllChipsDefault(): Boolean? {
        var tag = ""
        peopleChipGroup.children.iterator().forEach {
            if (tag == "") {
                tag = it.tag as? String ?: return null
            }
            if (it.tag != tag) return null
        }

        return tag == "default"
    }


    private fun setupProgressDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress_dialog)
        progressDialog = builder.create()
    }

    private fun showProgressDialog() {
        progressDialog.show()
    }

    private fun setupTouch() {

        cancelButton.setOnClickListener {
            cancelDialog()
        }

        saveEventButton.setOnClickListener {
            saveEventButton.animateChange(
                200,
                fromScaleX = 1F,
                toScaleX = 1.2F,
                fromScaleY = 1F,
                toScaleY = 0.7F
            )
            handleSaveEvent()
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

        addToCalendarSection.setOnClickListener {
            addToCalendarCheckBox.isChecked = !addToCalendarCheckBox.isChecked
        }

        deleteEventSection.setOnClickListener { handleDeleteEvent() }
    }

    private fun cancelDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cancel")
            .setMessage("Do you want to cancel your draft?\nThe information will be lost.")
            .setNegativeButton("Keep editing") { _, _ -> }
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .show()
    }

    private fun handleChooseAllSwitch() {
        peopleChipGroup.children.iterator().forEach {
            val tvIt = (it as? TextView) ?: return@forEach

            val newTag = if (chooseAllSwitch.isChecked) "highlight" else "default"

            if (tvIt.tag == newTag) return@forEach

            tvIt.tag = newTag

            tvIt.adaptStyleToTag(tvIt.text.toString())
        }
    }

    private fun setupStartDatePicker() {

        val onDateSetListener =
            OnDateSetListener {
                    datePicker: DatePicker?,
                    year: Int,
                    month: Int,
                    day: Int,
                ->

                viewmodel.startDateSet(year, month, day)

            }

        val startDate = viewmodel.getCurrStartDateTime()
        startDate.month

        DatePickerDialog(
            this@AddEventActivity,
            onDateSetListener,
            startDate.year,
            startDate.monthValue - 1,
            startDate.dayOfMonth
        ).apply {
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun setupEndDatePicker() {

        val onDateSetListener =
            OnDateSetListener {
                    datePicker: DatePicker?,
                    year: Int,
                    month: Int,
                    day: Int,
                ->
                viewmodel.endDateSet(year, month, day)
            }

        val endDate = viewmodel.getCurrEndDateTime()

        DatePickerDialog(
            this@AddEventActivity,
            onDateSetListener,
            endDate.year,
            endDate.monthValue - 1,
            endDate.dayOfMonth
        ).apply {
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun setupStartTimePicker() {
        val onTimeSetListener =
            OnTimeSetListener {
                    timePicker: TimePicker?,
                    hour: Int,
                    minute: Int,
                ->
                viewmodel.startTimeSet(hour, minute)
            }

        val startTime = viewmodel.getCurrStartDateTime()

        TimePickerDialog(
            this@AddEventActivity,
            onTimeSetListener,
            startTime.hour,
            startTime.minute,
            true
        ).apply {
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun setupEndTimePicker() {
        val onTimeSetListener =
            OnTimeSetListener {
                    timePicker: TimePicker?,
                    hour: Int,
                    minute: Int,
                ->

                viewmodel.endTimeSet(hour, minute)

            }

        val endTime = viewmodel.getCurrEndDateTime()

        TimePickerDialog(
            this@AddEventActivity,
            onTimeSetListener,
            endTime.hour,
            endTime.minute,
            true
        ).apply {
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun handleSaveEvent() {

        if (chooseAllSwitch.isChecked) {
            //todo: make sure everything is checked
        }

        showProgressDialog()

        lifecycleScope.launch {


            viewmodel.setTitle(titleEditText.text.toString())
            viewmodel.setLocation(locationEditText.text.toString())
            viewmodel.setSummary(summaryEditText.text.toString())
            viewmodel.updateOrInsertEvent()


            viewmodel.postEventState
                .collect {

                    progressDialog.dismiss()

                    it.doIfFailure {
                        Toast.makeText(
                            this@AddEventActivity,
                            "Error, ${it.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    it.doIfSucces {
                        Toast.makeText(this@AddEventActivity, "Event added!", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
        }


    }


    private fun handleDeleteEvent() {
        lifecycleScope.launch {
            viewmodel.deleteEvent()

            viewmodel.deleteEventState.collect { result ->

                result.doIfFailure {
                    Toast.makeText(
                        this@AddEventActivity,
                        it.localizedMessage ?: "An error occured with deleting event.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                result.doIfSucces {
                    Toast.makeText(this@AddEventActivity, "Event deleted!", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }

            }
        }
    }


    private fun getIntentEventId(): Int? {
        val eventId = intent.getIntExtra("eventId", -550)
        return if (eventId != -550) eventId else null
    }

}



