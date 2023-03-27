package com.example.eventusa.screens.settings.view

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.eventusa.R
import com.example.eventusa.utils.LocalStorageManager
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    lateinit var dateBeforeTodayLayout: LinearLayout
    lateinit var dateBeforeTodaySwitch: SwitchMaterial


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        dateBeforeTodayLayout = findViewById(R.id.dateBeforeTodayLayout)
        dateBeforeTodaySwitch = findViewById(R.id.dateBeforeTodaySwitch)

        loadSettings()
        handleClicks()
    }

    private fun loadSettings(){
        dateBeforeTodaySwitch.isChecked = LocalStorageManager.readAskConfirmDateBefore(this@SettingsActivity)
    }

    private fun handleClicks(){
        dateBeforeTodayLayout.setOnClickListener {
            dateBeforeTodaySwitch.performClick()
        }

        dateBeforeTodaySwitch.setOnClickListener {
            if(dateBeforeTodaySwitch.isChecked){
                LocalStorageManager.setCheckedAskConfirmDateBefore(this@SettingsActivity)
            }else{
                LocalStorageManager.setUncheckedAskConfirmDateBefore(this@SettingsActivity)
            }
        }
    }



}