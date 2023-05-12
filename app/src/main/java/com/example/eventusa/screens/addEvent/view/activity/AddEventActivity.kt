package com.example.eventusa.screens.addEvent.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.eventusa.R
import com.example.eventusa.app.EventusaApplication
import com.example.eventusa.screens.addEvent.view.recycler_utils.NotificationsRecyclerAdapter
import com.example.eventusa.screens.addEvent.viewmodel.AddEventViewModel
import com.example.eventusa.screens.addEvent.viewmodel.AddEventViewModelFactory
import com.example.eventusa.screens.events.view.EventsActivity
import com.example.eventusa.screens.login.view.LoginActivity
import com.google.android.material.chip.ChipGroup


class AddEventActivity : AppCompatActivity() {

    val viewmodel: AddEventViewModel by viewModels { AddEventViewModelFactory((application as EventusaApplication).eventsRepository) }

    lateinit var addEventActivityLayout: LinearLayout

    lateinit var progressDialog: AlertDialog
    lateinit var progressDialogView: View
    var chooseNotifDialog: androidx.appcompat.app.AlertDialog? = null
    var chooseColorDialog: androidx.appcompat.app.AlertDialog? = null

    var isActivityFromNotif = false
    var isActivityEditEvent = false



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

        handleIsFromNotif()

        bindViews()

        setupUI()

        setupNotificationSection()
        setupAddOrEditState()
        setupStateObserving()

    }

    private fun setupUI() {
        setupPeopleChips()
        setupProgressDialog()
        setupTouch()
    }


    private fun setupProgressDialog() {
        val builder = AlertDialog.Builder(this)
        progressDialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(progressDialogView)
        progressDialog = builder.create()
    }

    fun showProgressDialog(loadingMessage: String) {
        progressDialogView.findViewById<TextView>(R.id.loadingTextView).text = loadingMessage
        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.dismiss()
    }


    private fun setupAddOrEditState() {
        val eventId = getIntentEventId()
        isActivityEditEvent = (eventId != null)
        if (eventId != null) {
            handleEditEvent(eventId)
        } else {
            handleNewEvent()
        }
    }

    private fun handleEditEvent(eventId: Int) {
        viewmodel.fetchEvent(eventId)
    }

    private fun handleNewEvent() {
        deleteEventSection.visibility = View.GONE
        deleteEventSectionDivider.visibility = View.GONE
    }

    private fun setupStateObserving() {

        setupSaveEventFlowObserving()

        setupNotificationStateObserving()

        setupFetchEventStateObserving()

    }


    fun gotoEventsScreen() {
        val intent = Intent(this@AddEventActivity, EventsActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun gotoLoginScreen() {
        val intent = Intent(this@AddEventActivity, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {
        cancelButton.performClick()
    }

}





