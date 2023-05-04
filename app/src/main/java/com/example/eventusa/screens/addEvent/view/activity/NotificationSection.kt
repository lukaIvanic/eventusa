package com.example.eventusa.screens.addEvent.view.activity

import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.eventusa.app.EventusaApplication
import com.example.eventusa.caching.room.extraentities.EventNotification
import com.example.eventusa.screens.addEvent.data.NotificationPreset
import com.example.eventusa.screens.addEvent.view.recycler_utils.NotificationsAdapterEvents
import com.example.eventusa.screens.addEvent.view.recycler_utils.NotificationsRecyclerAdapter
import com.example.eventusa.utils.extensions.doIfFailure
import com.example.eventusa.utils.extensions.doIfSucces
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


fun AddEventActivity.handleIsFromNotif(){
    isActivityFromNotif = getIntentIsFromNotif()

    if(isActivityFromNotif){
        if((application as? EventusaApplication)?.userRepository?.isUserAlreadyLoggedIn()?.not() == true){
            gotoLoginScreen()
            finish()
        }

    }
}


fun AddEventActivity.getIntentIsFromNotif(): Boolean {
    return intent.getBooleanExtra("is_from_notif", false)
}



fun AddEventActivity.setupNotificationSection() {
    notifsAdapter = NotificationsRecyclerAdapter()
    notifsRecyclerView.adapter = notifsAdapter

    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.CREATED) {
            notifsAdapter.cancelNotifFlow.collect { eventNotif ->
                notifsAdapter.deleteNotif(eventNotif)
                viewmodel.removeNotification(eventNotif)
            }
        }
    }
}

fun AddEventActivity.setupNotificationStateObserving() {

    lifecycleScope.launch {
        viewmodel.notificationsEventState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { result ->

                result.doIfFailure {
                    Toast.makeText(
                        this@setupNotificationStateObserving, it.localizedMessage, Toast.LENGTH_LONG
                    ).show()
                }

                result.doIfSucces {
                    when (it) {

                        is NotificationsAdapterEvents.LOAD_EVENTS -> {
                            notifsAdapter.initialLoadEvents(it.eventNotificationsList)
                        }

                        is NotificationsAdapterEvents.ADD_EVENT -> {
                            notifsAdapter.addNotif(it.eventNotification)
                        }

                        is NotificationsAdapterEvents.DELETE_EVENT -> {
                            notifsAdapter.deleteNotif(it.eventNotification)
                        }

                    }
                }

            }

    }
}


fun AddEventActivity.showChooseNotificationDialog() {
    val notifDialogBuilder =
        MaterialAlertDialogBuilder(this)
            .setCancelable(true)
            .setSingleChoiceItems(
                NotificationPreset.getPresetsDescs(),
                -1
            ) { _, index ->

                val minsBeforeEvent =
                    NotificationPreset.getPresetByIndex(index).notifTimeBeforeEventMins



                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                        val success = viewmodel.addNotification(minsBeforeEvent)
                        if (!success) return@repeatOnLifecycle

                        notifsAdapter.addNotif(
                            EventNotification(
                                eventId = 0,
                                minutesBeforeEvent = minsBeforeEvent
                            )
                        )
                    }
                }


                chooseNotifDialog?.dismiss()
            }


    chooseNotifDialog = notifDialogBuilder.show()
}