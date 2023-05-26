package com.example.eventusa.screens.events.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.eventusa.R
import com.example.eventusa.app.EventusaApplication
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.addEvent.view.activity.AddEventActivity
import com.example.eventusa.screens.events.EventsViewModel
import com.example.eventusa.screens.events.EventsViewModelFactory
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.screens.events.view.recycler_utils.EventsAdapter
import com.example.eventusa.screens.login.view.LoginActivity
import com.example.eventusa.screens.settings.view.SettingsActivity
import com.example.eventusa.utils.extensions.doIfFailure
import com.example.eventusa.utils.extensions.doIfSucces
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timerTask


class EventsActivity : AppCompatActivity() {

    private val viewModel: EventsViewModel by viewModels {
        EventsViewModelFactory(
            (application as EventusaApplication).eventsRepository,
            (application as EventusaApplication).userRepository
        )
    }

    lateinit var eventsActivityLayout: ConstraintLayout

    lateinit var welcomeNameTextView: TextView
    lateinit var logoutButton: ImageView
    lateinit var settingsButton: ImageView

    lateinit var progressBar: LinearProgressIndicator

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: EventsAdapter

    lateinit var newEventButton: FloatingActionButton

    var backPressedAlready = false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        eventsActivityLayout = findViewById(R.id.eventsActivityLayout)

        welcomeNameTextView = findViewById(R.id.settingsTitleTextView)
        setWelcomeName(LocalStorageManager.readUsername())

        logoutButton = findViewById(R.id.logoutButton)
        settingsButton = findViewById(R.id.settingsButton)

        progressBar = findViewById(R.id.progressBar)

        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = EventsAdapter(onEventClick = {
            onEventClick(it)
        }, onScrollNeeded = { position ->
            scrollToPosition(position)
       })
        recyclerView.adapter = recyclerAdapter

        newEventButton = findViewById(R.id.newEventFab)

        setupTouch()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventsUiState.collect { resultOf ->

                    animateProgressBar(
                        resultOf is ResultOf.Loading
                                && !swipeRefreshLayout.isRefreshing
                    )

                    if (resultOf !is ResultOf.Loading) {
                        swipeRefreshLayout.isRefreshing = false
                    }

                    resultOf.doIfSucces {
                        recyclerAdapter.updateEvents(it.eventsItemsList)
                    }

                    resultOf.doIfFailure {
                        showError(it.localizedMessage)
                    }

                }
            }
        }


    }

    private fun setupTouch() {
        logoutButton.setOnClickListener {
            doLogout()
        }

        settingsButton.setOnClickListener {
            gotoSettings()
        }

        newEventButton.setOnClickListener {
            onEventClick()
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshEvents()
        }
    }

    private fun animateProgressBar(isStartLoading: Boolean) {
        if (isStartLoading) {
            progressBar.animate()
                .scaleY(1F)
                .setDuration(1000)
                .start()
        } else {
            progressBar.animate()
                .scaleY(0F)
                .setDuration(1000)
                .start()
        }
    }

    fun onEventClick(rinetEvent: RINetEvent? = null) {
        val intent = Intent(this@EventsActivity, AddEventActivity::class.java)
        rinetEvent?.eventId?.let {
            intent.putExtra("event_id", it)
        }

        startActivity(intent)
    }

    private fun scrollToPosition(position: Int){

        val myLayoutManager = (recyclerView.layoutManager as LinearLayoutManager)


        // Check if event is visible
        if(myLayoutManager.findFirstVisibleItemPosition() <= position && myLayoutManager.findLastVisibleItemPosition() >= position){
            return
        }

        if(myLayoutManager.findFirstVisibleItemPosition() > position){
            recyclerView.smoothScrollToPosition(position - 1)
        }else{
            recyclerView.smoothScrollToPosition(position + 1)
        }

    }

    private fun setWelcomeName(s: String?) {
        welcomeNameTextView.text = if (s == null) {
            "Welcome!"
        } else {
            "Welcome $s"
        }

    }


    override fun onBackPressed() {

        if (!backPressedAlready) {
            Toast.makeText(this@EventsActivity, "Press again to exit.", Toast.LENGTH_SHORT).show()
            backPressedAlready = true
            Timer().schedule(timerTask {
                backPressedAlready = false
            }, 600)

        } else {
            super.onBackPressed()
        }
    }

    private fun doLogout() {

        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Your login info will be lost if you logout.")
            .setNegativeButton("Cancel") { _, _ ->
            }
            .setPositiveButton("Logout") { _, _ ->

                LocalStorageManager.turnOffRememberMe()
                LocalStorageManager.saveUserAndPass("", "")

                val intent = Intent(this@EventsActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()

    }

    private fun gotoSettings() {
        Intent(this@EventsActivity, SettingsActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun showError(message: String?) {
        Snackbar.make(eventsActivityLayout, message ?: "An error occured.", Snackbar.LENGTH_LONG)
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshEventsLocally()
    }

}