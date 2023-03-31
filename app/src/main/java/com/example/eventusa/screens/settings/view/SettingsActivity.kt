package com.example.eventusa.screens.settings.view

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventusa.R
import com.example.eventusa.utils.LocalStorageManager
import com.example.eventusa.utils.notifications.NotifManager
import com.google.android.material.switchmaterial.SwitchMaterial
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.random.Random


class SettingsActivity : AppCompatActivity() {

    lateinit var dateBeforeTodayLayout: LinearLayout
    lateinit var dateBeforeTodaySwitch: SwitchMaterial

    lateinit var notifTest: LinearLayout

    lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        dateBeforeTodayLayout = findViewById(R.id.dateBeforeTodayLayout)
        dateBeforeTodaySwitch = findViewById(R.id.dateBeforeTodaySwitch)

        notifTest = findViewById(R.id.notifTestLayout)

        loadSettings()
        handleClicks()
    }


    private fun loadSettings() {
        dateBeforeTodaySwitch.isChecked =
            LocalStorageManager.readAskConfirmDateBefore()
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


        notifTest.setOnClickListener {
            handleNotifTestClick()
        }
    }

    fun handleNotifTestClick() {
        Toast.makeText(
            this@SettingsActivity,
            "Notification scheduled in 10 seconds",
            Toast.LENGTH_LONG
        ).show()

        NotifManager(this@SettingsActivity).scheduleExactAlarm(
            Random.nextInt(0, 99999),
            "Test from settings",
            "13:00-14:00",
            LocalDateTime.now().plusSeconds(10L).atZone(
                ZoneId.systemDefault()
            ).toEpochSecond() * 1000L,
            1
        )

    }


}