package com.example.eventusa.screens.settings.view

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.eventusa.R
import com.example.eventusa.caching.sharedprefs.LocalStorageManager
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {

    lateinit var dateBeforeTodayLayout: LinearLayout
    lateinit var dateBeforeTodaySwitch: SwitchMaterial

    lateinit var showMultipleDayEventsLayout: LinearLayout
    lateinit var showMultipleDayEventsSwitch: SwitchMaterial


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        dateBeforeTodayLayout = findViewById(R.id.dateBeforeTodayLayout)
        dateBeforeTodaySwitch = findViewById(R.id.dateBeforeTodaySwitch)


        showMultipleDayEventsLayout = findViewById(R.id.showMultipleDayEventsLayout)
        showMultipleDayEventsSwitch = findViewById(R.id.showMultipleDayEventsSwitch)


        loadSettings()
        handleClicks()
    }


    private fun loadSettings() {
        dateBeforeTodaySwitch.isChecked =
            !LocalStorageManager.readAskConfirmDateBefore()

        showMultipleDayEventsSwitch.isChecked = LocalStorageManager.readShowMultipleDayEvents()
    }

    private fun handleClicks() {
        dateBeforeTodayLayout.setOnClickListener {
            dateBeforeTodaySwitch.performClick()
        }

        dateBeforeTodaySwitch.setOnClickListener {
            if (!dateBeforeTodaySwitch.isChecked) {
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

    }


}