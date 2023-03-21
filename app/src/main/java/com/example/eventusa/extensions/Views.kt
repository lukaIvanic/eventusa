package com.example.eventusa.extensions

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop


fun View.setCustomMargins(
    left: Float = this.marginLeft.toFloat(),
    top: Float = this.marginTop.toFloat(),
    right: Float = this.marginRight.toFloat(),
    bottom: Float = this.marginBottom.toFloat(),
) {

    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(dpToPx(left), dpToPx(top), dpToPx(right), dpToPx(bottom))
    }
}

fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
fun Context.dpToPx(dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
