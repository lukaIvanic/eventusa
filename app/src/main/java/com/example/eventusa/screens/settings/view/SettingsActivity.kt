package com.example.eventusa.screens.settings.view

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.eventusa.R
import com.example.eventusa.caching.room.Room
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch


class SettingsActivity : AppCompatActivity() {

    lateinit var dateBeforeTodayLayout: LinearLayout
    lateinit var dateBeforeTodaySwitch: SwitchMaterial

    lateinit var showMultipleDayEventsLayout: LinearLayout
    lateinit var showMultipleDayEventsSwitch: SwitchMaterial

    lateinit var notifTest: LinearLayout
    lateinit var readDbTest: LinearLayout

    lateinit var alarmManager: AlarmManager

    val notifDelaySeconds = 10L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        dateBeforeTodayLayout = findViewById(R.id.dateBeforeTodayLayout)
        dateBeforeTodaySwitch = findViewById(R.id.dateBeforeTodaySwitch)


        showMultipleDayEventsLayout = findViewById(R.id.showMultipleDayEventsLayout)
        showMultipleDayEventsSwitch = findViewById(R.id.showMultipleDayEventsSwitch)


        notifTest = findViewById(R.id.notifTestLayout)
        readDbTest = findViewById(R.id.readDbTestLayout)

        loadSettings()
        handleClicks()
    }


    private fun loadSettings() {
        dateBeforeTodaySwitch.isChecked =
            LocalStorageManager.readAskConfirmDateBefore()

        showMultipleDayEventsSwitch.isChecked = LocalStorageManager.readShowMultipleDayEvents()
    }

    private fun handleClicks() {
        dateBeforeTodayLayout.setOnClickListener {
            dateBeforeTodaySwitch.performClick()
        }

        dateBeforeTodaySwitch.setOnClickListener {
            if (dateBeforeTodaySwitch.isChecked) {
                LocalStorageManager.setCheckedAskConfirmDateBefore()
            } else {
                LocalStorageManager.setUncheckedAskConfirmDateBefore()
            }
        }

        showMultipleDayEventsLayout.setOnClickListener {
            showMultipleDayEventsSwitch.performClick()
        }

        showMultipleDayEventsSwitch.setOnClickListener {
            if (showMultipleDayEventsSwitch.isChecked) {
                LocalStorageManager.setCheckedShowMultipleDayEvents()
            } else {
                LocalStorageManager.setUncheckedShowMultipleDayEvents()
            }
        }


        notifTest.setOnClickListener {
            Toast.makeText(
                this@SettingsActivity,
                "Notification scheduled in 10 seconds",
                Toast.LENGTH_LONG
            ).show()

//            NotifManager(this@SettingsActivity).scheduleExactAlarm(
//                Random.nextInt(0, 99999),
//                "Test from settings",
//                "13:00-14:00",
//                LocalDateTime.now().plusSeconds(10L).atZone(
//                    ZoneId.systemDefault()
//                ).toEpochSecond() * 1000L,
//                1
//            )
        }

        readDbTest.setOnClickListener {
            handleReadDbTest()
        }
    }

    fun handleReadDbTest(){

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.CREATED){
                val eventsList = Room.readAllEvents()
                eventsList.forEach {
                    Log.i("LUKA DB READ", "title: ${it.title}")
                }
            }
        }

    }


}