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

    lateinit var randomColorWhenCreatingEventLayout: LinearLayout
    lateinit var randomColorWhenCreatingEventSwitch: SwitchMaterial


    lateinit var showFullColorsInAppLayout: LinearLayout
    lateinit var showFullColorsInAppSwitch: SwitchMaterial


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        dateBeforeTodayLayout = findViewById(R.id.dateBeforeTodayLayout)
        dateBeforeTodaySwitch = findViewById(R.id.dateBeforeTodaySwitch)

        showMultipleDayEventsLayout = findViewById(R.id.showMultipleDayEventsLayout)
        showMultipleDayEventsSwitch = findViewById(R.id.showMultipleDayEventsSwitch)

        randomColorWhenCreatingEventLayout = findViewById(R.id.generateColorWhenCreatingEventLayout)
        randomColorWhenCreatingEventSwitch = findViewById(R.id.generateColorWhenCreatingEventSwitch)

        showFullColorsInAppLayout = findViewById(R.id.showFullColorsInAppLayout)
        showFullColorsInAppSwitch = findViewById(R.id.showFullColorsInAppSwitch)

        loadSettings()
        handleClicks()
    }


    private fun loadSettings() {
        dateBeforeTodaySwitch.isChecked =
            !LocalStorageManager.readAskConfirmDateBefore()

        showMultipleDayEventsSwitch.isChecked = LocalStorageManager.readShowMultipleDayEvents()

        randomColorWhenCreatingEventSwitch.isChecked =
            LocalStorageManager.readRandomColorWhenCreatingEvent()

        showFullColorsInAppSwitch.isChecked = LocalStorageManager.readShowFullColorsInApp()
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

        randomColorWhenCreatingEventLayout.setOnClickListener {
            randomColorWhenCreatingEventSwitch.performClick()
        }

        randomColorWhenCreatingEventSwitch.setOnClickListener {
            if (randomColorWhenCreatingEventSwitch.isChecked) {
                LocalStorageManager.setCheckedRandomColorWhenCreatingEvent()
            } else {
                LocalStorageManager.setUncheckedRandomColorWhenCreatingEvent()
            }
        }

        showFullColorsInAppLayout.setOnClickListener {
            showFullColorsInAppSwitch.performClick()
        }

        showFullColorsInAppSwitch.setOnClickListener {
            if (showFullColorsInAppSwitch.isChecked) {
                LocalStorageManager.setCheckedShowFullColorsInApp()
            } else {
                LocalStorageManager.setUncheckedShowFullColorsInApp()
            }
        }

    }


}