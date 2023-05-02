package com.example.eventusa.screens.addEvent.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.eventusa.R
import com.example.eventusa.app.EventusaApplication
import com.example.eventusa.caching.room.extraentities.EventNotification
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.addEvent.data.NotificationPreset
import com.example.eventusa.screens.addEvent.view.recycler_utils.NotificationsAdapterEvents
import com.example.eventusa.screens.addEvent.view.recycler_utils.NotificationsRecyclerAdapter
import com.example.eventusa.screens.addEvent.viewmodel.AddEventViewModel
import com.example.eventusa.screens.addEvent.viewmodel.AddEventViewModelFactory
import com.example.eventusa.screens.events.data.EventColors
import com.example.eventusa.screens.login.model.User
import com.example.eventusa.utils.*
import com.example.eventusa.utils.extensions.*
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate


class AddEventActivity : AppCompatActivity() {

    val viewmodel: AddEventViewModel by viewModels { AddEventViewModelFactory((application as EventusaApplication).eventsRepository) }

    lateinit var progressDialog: AlertDialog
    var chooseNotifDialog: androidx.appcompat.app.AlertDialog? = null
    var chooseColorDialog: androidx.appcompat.app.AlertDialog? = null

    lateinit var saveEventButton: TextView
    lateinit var cancelButton: ImageView

    lateinit var titleEditText: EditText

    lateinit var addToCalendarSection: LinearLayout
    lateinit var addToCalendarCheckBox: CheckBox

    lateinit var startDateTextView: TextView
    lateinit var startTimeTextView: TextView
    lateinit var endDateTextView: TextView
    lateinit var endTimeTextView: TextView

    lateinit var chooseAllSection: ConstraintLayout
    lateinit var chooseAllSwitch: Switch
    lateinit var peopleChipGroup: ChipGroup

    lateinit var addNotificationButton: TextView
    lateinit var notifsRecyclerView: RecyclerView
    lateinit var notifsAdapter: NotificationsRecyclerAdapter

    lateinit var locationEditText: EditText
    lateinit var summaryEditText: EditText

    lateinit var chooseColorSection: LinearLayout
    lateinit var chooseColorCircle: CardView
    lateinit var eventColorTextView: TextView

    lateinit var deleteEventSection: LinearLayout
    lateinit var deleteEventSectionDivider: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

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

        setupNotificationSection()

        chooseColorSection = findViewById(R.id.chooseColorSection)
        chooseColorCircle = findViewById(R.id.chooseColorCircle)
        eventColorTextView = findViewById(R.id.eventColorTextView)


        deleteEventSection = findViewById(R.id.deleteEventSection)
        deleteEventSectionDivider = findViewById(R.id.deleteEventSectionDivider)

        setupUI()
        setupStateObserving()

    }

    private fun setupStateObserving() {

        val eventId = getIntentEventId()
        if (eventId != null) {
            handleEditEvent(eventId)
        } else {
            handleNewEvent()
        }

        lifecycleScope.launch {
            viewmodel.postEventState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { result ->
                    if (result is ResultOf.Loading) {
                        showProgressDialog()
                    } else {
                        hideProgressDialog()
                    }

                    result.doIfFailure {
                        Toast.makeText(
                            this@AddEventActivity,
                            it.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    result.doIfSucces {
                        finish()
                    }
                }
        }

        lifecycleScope.launch {
            viewmodel.notificationsEventState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { result ->

                    result.doIfFailure {
                        Toast.makeText(
                            this@AddEventActivity, it.localizedMessage, Toast.LENGTH_LONG
                        ).show()
                    }

                    result.doIfSucces {
                        when (it) {

                            is NotificationsAdapterEvents.LOAD_EVENTS -> {
                                notifsAdapter.initialLoadEvents(it.eventNotificationsList)
                            }

                            is NotificationsAdapterEvents.ADD_EVENT -> {
                                notifsAdapter.addNotif(it.eventNotification)
                            }

                            is NotificationsAdapterEvents.DELETE_EVENT -> {
                                notifsAdapter.deleteNotif(it.eventNotification)
                            }

                        }
                    }

                }

        }

        lifecycleScope.launch {

            viewmodel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { result ->
                    result.doIfLoading {
                        Toast.makeText(
                            this@AddEventActivity, "Loading event..", Toast.LENGTH_LONG
                        ).show()
                    }

                    result.doIfFailure {
                        Toast.makeText(
                            this@AddEventActivity, it.localizedMessage, Toast.LENGTH_LONG
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

                            if (locationEditText.text.isEmpty()) locationEditText.setText(location)

                            if (summaryEditText.text.isEmpty()) summaryEditText.setText(description)


                            var allChipsHighlighted = true

                            peopleChipGroup.children.iterator().withIndex().forEach { indexedChip ->
                                val chipTv = indexedChip.value as? TextView ?: return@forEach
                                val user =
                                    usersAttending.firstOrNull { it.name == chipTv.text.toString() }

                                if (user != null) {
                                    chipTv.setChipHighlighted(user.name ?: "")

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

    private fun handleEditEvent(eventId: Int) {
        viewmodel.fetchEvent(eventId)
    }

    private fun handleNewEvent() {
        deleteEventSection.visibility = View.GONE
        deleteEventSectionDivider.visibility = View.GONE
    }

    private fun setupUI() {
        setupPeopleChips()
        setupProgressDialog()
        setupTouch()
    }

    private fun setupNotificationSection() {
        addNotificationButton = findViewById(R.id.addNotificationButton)

        notifsRecyclerView = findViewById(R.id.notificationsRecyclerView)
        notifsAdapter = NotificationsRecyclerAdapter()
        notifsRecyclerView.adapter = notifsAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                notifsAdapter.cancelNotifFlow.collect { eventNotif ->
                    notifsAdapter.deleteNotif(eventNotif)
                    viewmodel.removeNotification(eventNotif)
                }
            }
        }
    }

    private fun setupPeopleChips() {


        User.getAllUsers().forEach {
            addRinetChip(it)
        }

    }

    private fun addRinetChip(user: User) {
        val textView = TextView(this)

        peopleChipGroup.addView(textView)

        textView.apply {
            text = user.name
            setChipDefault(user.name ?: "", animate = false)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
            setPadding(dpToPx(10F), dpToPx(8F), dpToPx(10F), dpToPx(8F))
//            updateChipStyle(ChipStatus.DEFAULT)


            setOnClickListener {

                if (handleChipClick(user)) {
                    updateChipStyle(ChipStatus.HIGHLIGHTED)
                } else {
                    updateChipStyle(ChipStatus.DEFAULT)
                }


            }
        }

    }

    private fun handleChipClick(user: User): Boolean {

        val isChipHighlight = viewmodel.userChipClicked(user)

        chooseAllSwitch.isChecked = viewmodel.isAllUserChipsActivated()

        return isChipHighlight
    }

    private fun setupProgressDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress_dialog)
        progressDialog = builder.create()
    }

    private fun showProgressDialog() {
        progressDialog.show()
    }

    private fun hideProgressDialog() {
        progressDialog.hide()
    }

    private fun setupTouch() {

        cancelButton.setOnClickListener {
            showCancelEditDialog()
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

    private fun showCancelEditDialog() {
        MaterialAlertDialogBuilder(this).setTitle("Cancel")
            .setMessage("Do you want to cancel your draft?\nThe information will be lost.")
            .setNegativeButton("Keep editing") { _, _ -> }.setPositiveButton("OK") { _, _ ->
                viewmodel.resetDefaultUiState()
                finish()
            }.show()
    }

    private fun showChooseNotificationDialog() {
        val notifDialogBuilder =
            MaterialAlertDialogBuilder(this)
                .setCancelable(true)
                .setSingleChoiceItems(
                    NotificationPreset.getPresetsDescs(),
                    -1
                ) { _, index ->

                    val minsBeforeEvent =
                        NotificationPreset.getPresetByIndex(index).notifTimeBeforeEventMins



                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.CREATED) {
                            val success = viewmodel.addNotification(minsBeforeEvent)
                            if (!success) return@repeatOnLifecycle

                            notifsAdapter.addNotif(
                                EventNotification(
                                    eventId = 0,
                                    minutesBeforeEvent = minsBeforeEvent
                                )
                            )
                        }
                    }


                    chooseNotifDialog?.dismiss()
                }


        chooseNotifDialog = notifDialogBuilder.show()
    }

    class Item (val text: String, var drawableId: Int){
        override fun toString(): String {
            return text
        }
    }

    private fun showChooseColorDialog() {

        val items = EventColors.getColorItems()

        val pos = viewmodel.getEventColor()

        items[pos].drawableId = EventColors.getDrawableIdFull(pos)


        val adapter: ListAdapter = object : ArrayAdapter<Item?>(
            this,
            R.layout.select_dialog_item,
            R.id.colorItemTextView,
            items
        ) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                val view = super.getView(position, convertView, parent)

                val tv = view.findViewById<TextView>(R.id.colorItemTextView)

                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items.get(position).drawableId, 0, 0, 0)
                tv.compoundDrawablePadding = dpToPx(16F)
                return view
            }
        }


        val notifDialogBuilder =
            MaterialAlertDialogBuilder(this)
                .setCancelable(true)
                .setAdapter(adapter) { _, index ->

                    viewmodel.setEventColor(index)

                    updateEventCircleColor(index)


                    chooseColorDialog?.dismiss()
                }


        chooseColorDialog = notifDialogBuilder.show()
    }

    private fun updateEventCircleColor(index: Int) {
        chooseColorCircle.setCardBackgroundColor(
            resources.getColor(
                EventColors.getColorId(
                    index
                )
            )
        )

        eventColorTextView.text = EventColors.getPresets().get(index)

    }

    private fun handleChooseAllSwitch() {

        viewmodel.selectAllUserChips(chooseAllSwitch.isChecked)


        peopleChipGroup.children.iterator().forEach {
            (it as? TextView)?.let { tv ->

                if (chooseAllSwitch.isChecked) {
                    if (tv.tag != "highlighted")
                        tv.updateChipStyle(ChipStatus.HIGHLIGHTED)
                } else {
                    tv.updateChipStyle(ChipStatus.DEFAULT)
                }

            }

        }
    }


    private fun setupStartDatePicker() {

        val onDateSetListener = OnDateSetListener {
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

    private fun confirmDateBeforeTodayDialog(year: Int, month: Int, day: Int) {

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

    private fun handleSetStartDate(year: Int, month: Int, day: Int) {
        viewmodel.startDateSet(year, month, day)
    }

    private fun setupEndDatePicker() {

        val onDateSetListener = OnDateSetListener {
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
        val onTimeSetListener = OnTimeSetListener {
                timePicker: TimePicker?,
                hour: Int,
                minute: Int,
            ->
            viewmodel.startTimeSet(hour, minute)
        }

        val startTime = viewmodel.getCurrStartDateTime()

        TimePickerDialog(
            this@AddEventActivity, onTimeSetListener, startTime.hour, startTime.minute, true
        ).apply {
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun setupEndTimePicker() {
        val onTimeSetListener = OnTimeSetListener {
                timePicker: TimePicker?,
                hour: Int,
                minute: Int,
            ->

            viewmodel.endTimeSet(hour, minute)

        }

        val endTime = viewmodel.getCurrEndDateTime()

        TimePickerDialog(
            this@AddEventActivity, onTimeSetListener, endTime.hour, endTime.minute, true
        ).apply {
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun handleSaveEvent() {


        showProgressDialog()

        lifecycleScope.launch {


            viewmodel.setTitle(titleEditText.text.toString())
            viewmodel.setLocation(locationEditText.text.toString())
            viewmodel.setSummary(summaryEditText.text.toString())

            viewmodel.updateOrInsertEvent(this@AddEventActivity)

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
                    //TODO: handle notification deletion
                    finish()
                }

            }
        }
    }


    private fun getIntentEventId(): Int? {
        val eventId = intent.getIntExtra("event_id", -550)
        return if (eventId != -550) eventId else null
    }

    override fun onBackPressed() {
        cancelButton.performClick()
    }

}





