package com.example.eventusa.utils.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmNotificationDismissedBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
//        val alarmRingtoneState = (context.applicationContext as StuddyApplication).alarmRingtoneState
//        alarmRingtoneState.value?.stop()
//        alarmRingtoneState.value = null
    }
}