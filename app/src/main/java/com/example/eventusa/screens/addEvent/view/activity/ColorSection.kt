package com.example.eventusa.screens.addEvent.view.activity

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.TextView
import com.example.eventusa.R
import com.example.eventusa.screens.events.data.EventColors
import com.example.eventusa.utils.extensions.dpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class Item (val text: String, var drawableId: Int){
    override fun toString(): String {
        return text
    }
}

fun AddEventActivity.showChooseColorDialog() {

    val items = EventColors.getColorItems()

    val pos = viewmodel.getEventColor()

    items[pos].drawableId = EventColors.getDrawableIdFull(pos)


    val adapter: ListAdapter = object : ArrayAdapter<Item?>(
        this,
        R.layout.select_dialog_item,
        R.id.colorItemTextView,
        items
    ) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val view = super.getView(position, convertView, parent)

            val tv = view.findViewById<TextView>(R.id.colorItemTextView)

            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)

            //Put the image on the TextView
            tv.setCompoundDrawablesWithIntrinsicBounds(items.get(position).drawableId, 0, 0, 0)
            tv.compoundDrawablePadding = dpToPx(16F)
            return view
        }
    }


    val notifDialogBuilder =
        MaterialAlertDialogBuilder(this)
            .setCancelable(true)
            .setAdapter(adapter) { _, index ->

                viewmodel.setEventColor(index)

                updateEventCircleColor(index)


                chooseColorDialog?.dismiss()
            }


    chooseColorDialog = notifDialogBuilder.show()
}

fun AddEventActivity.updateEventCircleColor(index: Int) {
    chooseColorCircle.setCardBackgroundColor(
        resources.getColor(
            EventColors.getColorId(
                index
            )
        )
    )

    eventColorTextView.text = EventColors.getPresets().get(index)

}