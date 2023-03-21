package com.example.eventusa.screens.events.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventusa.R
import com.example.eventusa.app.EventusaApplication
import com.example.eventusa.extensions.doIfFailure
import com.example.eventusa.extensions.doIfSucces
import com.example.eventusa.network.ResultOf
import com.example.eventusa.screens.addEvent.view.AddEventActivity
import com.example.eventusa.screens.events.EventsViewModel
import com.example.eventusa.screens.events.EventsViewModelFactory
import com.example.eventusa.screens.events.data.RINetEvent
import com.example.eventusa.screens.events.view.recycler_utils.EventsAdapter
import com.example.eventusa.screens.login.view.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.flow.collectLatest
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

    lateinit var nameTextView: TextView
    lateinit var logoutButton: ImageView

    lateinit var progressBar: LinearProgressIndicator

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: EventsAdapter

    lateinit var newEventButton: FloatingActionButton

    var backPressedAlready = false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        nameTextView = findViewById(R.id.welcomeNameTextView)
        nameTextView.text = "Welcome, ${viewModel.getUsername()}"

        logoutButton = findViewById(R.id.logoutButton)

        progressBar = findViewById(R.id.progressBar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = EventsAdapter { onEventClick(it) }
        recyclerView.adapter = recyclerAdapter

        newEventButton = findViewById(R.id.newEventFab)

        logoutButton.setOnClickListener {
            doLogout()
        }

        newEventButton.setOnClickListener {
            onEventClick()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventsUiState.collectLatest { resultOf ->

                    animateProgressBar(resultOf is ResultOf.Loading)

                    resultOf.doIfSucces {
                        recyclerAdapter.updateEvents(it.eventsItemsList)
                    }

                    resultOf.doIfFailure {
                        Toast.makeText(
                            this@EventsActivity,
                            "An error occured. ${it.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }
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
            intent.putExtra("eventId", it)
        }

        startActivity(intent)
    }


    override fun onBackPressed() {

        if (!backPressedAlready) {
            Toast.makeText(this@EventsActivity, "Press again to exit.", Toast.LENGTH_SHORT).show()
            backPressedAlready = true
            Timer().schedule(timerTask {
                backPressedAlready = false
            }, 100)

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

                val intent = Intent(this@EventsActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()

    }
}