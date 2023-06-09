package com.example.eventusa.screens.addEvent.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.eventusa.R
import com.example.eventusa.app.EventusaApplication
import com.example.eventusa.screens.addEvent.view.recycler_utils.NotificationsRecyclerAdapter
import com.example.eventusa.screens.addEvent.viewmodel.AddEventViewModel
import com.example.eventusa.screens.addEvent.viewmodel.AddEventViewModelFactory
import com.example.eventusa.screens.events.data.EventColors
import com.example.eventusa.screens.events.view.EventsActivity
import com.example.eventusa.screens.login.view.LoginActivity
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId


class AddEventActivity : AppCompatActivity() {

    val viewmodel: AddEventViewModel by viewModels {
        AddEventViewModelFactory(
            (application as EventusaApplication).eventsRepository,
            (application as EventusaApplication).userRepository
        )
    }

    lateinit var
            addEventActivityLayout: LinearLayout

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

    lateinit var locationSectionLayout: ConstraintLayout
    lateinit var locationEditText: TextView


    lateinit var addNotificationButton: TextView
    lateinit var notifsRecyclerView: RecyclerView
    lateinit var notifsAdapter: NotificationsRecyclerAdapter

    var startAutocomplete : ActivityResultLauncher<Intent>? = null

    lateinit var summaryEditText: EditText

    lateinit var chooseColorSection: LinearLayout
    lateinit var chooseColorCircle: CardView
    lateinit var eventColorTextView: TextView

    lateinit var deleteEventSection: LinearLayout
    lateinit var deleteEventSectionDivider: View

    var timestampLastMessage = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        handleIsFromNotif()

        bindViews()

        setupLocationMapsApi()

        setupUI()

        setupNotificationSection()
        setupAddOrEditState()
        setupStateObserving()

    }

    private fun setupStateObserving() {

        setupSaveEventFlowObserving()

        setupNotificationStateObserving()

        setupFetchEventStateObserving()

    }


    private fun setupUI() {
        setupPeopleChips()
        setupProgressDialog()
        setupTouch()
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
        showProgressDialog("Fetching event..")
        if(eventId != -1)
            viewmodel.fetchEvent(eventId)

    }

    private fun handleNewEvent() {
        deleteEventSection.visibility = View.GONE
        deleteEventSectionDivider.visibility = View.GONE
    }
    private fun setupProgressDialog() {
        val builder = AlertDialog.Builder(this)
        progressDialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(progressDialogView)
        progressDialog = builder.create()
        progressDialog.setCancelable(false)
    }

    fun showProgressDialog(loadingMessage: String) = lifecycleScope.launch {
        progressDialogView.findViewById<TextView>(R.id.loadingTextView).text = loadingMessage
        progressDialogView.findViewById<ProgressBar>(R.id.loader).indeterminateTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@AddEventActivity,
                    EventColors.getColorId(viewmodel.getEventColor())
                )
            )

        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.dismiss()
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





