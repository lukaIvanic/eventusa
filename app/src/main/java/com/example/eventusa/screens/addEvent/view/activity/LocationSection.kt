package com.example.eventusa.screens.addEvent.view.activity

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

fun AddEventActivity.setupLocationMapsApi(){

    startAutocomplete = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

        if (result.resultCode == Activity.RESULT_CANCELED) {
            showToast("Didn't receive location information.")
            return@registerForActivityResult
        }

        if(result.resultCode != Activity.RESULT_OK){
            showToast("Can't reach Google Maps API.")
            return@registerForActivityResult
        }

        if (result.resultCode == Activity.RESULT_OK) {

            val intent = result.data ?: return@registerForActivityResult
            val place = Autocomplete.getPlaceFromIntent(intent)
            locationEditText.setText("${place.name}, ${place.address}")

        }
    }


}

fun AddEventActivity.openLocationPickDialog() {

    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)

    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
        .build(this)
    startAutocomplete?.launch(intent)


}
