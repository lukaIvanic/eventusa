package com.example.eventusa.screens.addEvent.view.activity

import android.util.TypedValue
import android.widget.TextView
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import com.example.eventusa.screens.login.model.User
import com.example.eventusa.utils.ChipStatus
import com.example.eventusa.utils.extensions.dpToPx
import com.example.eventusa.utils.setChipDefault
import com.example.eventusa.utils.setChipHighlighted
import com.example.eventusa.utils.updateChipStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun AddEventActivity.setupPeopleChips() {


    lifecycleScope.launch {

        viewmodel.getAllUsers().forEach { user -> addRinetChip(user) }
        peopleChipGroup.children.iterator().withIndex().forEach { indexedChip ->
            val chipTv = indexedChip.value as? TextView ?: return@forEach
            val user =
                viewmodel.getAttendingUsers()
                    .firstOrNull { it.displayName == chipTv.text.toString() }

            if (user != null) {
                chipTv.setChipHighlighted(user.displayName)

                // Animation eye sugar
                val delayTime = 100 / (indexedChip.index / 2).plus(1)
                delay(delayTime.toLong())
            }

        }

    }

}


fun AddEventActivity.addRinetChip(user: User) {
    val textView = TextView(this)

    peopleChipGroup.addView(textView)

    textView.apply {
        text = user.displayName
        setChipDefault(user.displayName ?: "", animate = false)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
        setPadding(dpToPx(10F), dpToPx(8F), dpToPx(10F), dpToPx(8F))



        setOnClickListener {

            if (handleChipClick(user)) {
                updateChipStyle(ChipStatus.HIGHLIGHTED)
            } else {
                updateChipStyle(ChipStatus.DEFAULT)
            }


        }
    }

}

fun AddEventActivity.handleChipClick(user: User): Boolean {

    val isChipHighlight = viewmodel.userChipClicked(user)

    chooseAllSwitch.isChecked = viewmodel.isAllUserChipsActivated()

    return isChipHighlight
}

fun AddEventActivity.handleChooseAllSwitch() {

    viewmodel.selectAllUserChips(chooseAllSwitch.isChecked)


    peopleChipGroup.children.iterator().forEach {
        (it as? TextView)?.let { tv ->

            if (chooseAllSwitch.isChecked) {
                if (tv.tag != "highlighted")
                    tv.updateChipStyle(ChipStatus.HIGHLIGHTED)
            } else {
                tv.updateChipStyle(ChipStatus.DEFAULT)
            }

        }

    }
}
