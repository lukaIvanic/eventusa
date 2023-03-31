package com.example.eventusa.utils

import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.TextView
import com.example.eventusa.R
import com.example.eventusa.extensions.dpToPx

/**
    * Default animation, used only in AddEventActivity
    * @see com.example.eventusa.screens.addEvent.view.AddEventActivity
*/
fun TextView.setTextAnimated(
    newText: String,
    totalDuration: Long = 400,
    yFirstTo: Float = -15F,
    ySecondFrom: Float = 10F,
) {

    if (this.text == newText) return

    this.animate()
        .setDuration(totalDuration/2)
        .alpha(0F)
        .translationY(yFirstTo)
        .setInterpolator(AccelerateInterpolator())
        .withEndAction {
            this.text = newText
            this.translationY = ySecondFrom
            this.animate()
                .alpha(1F)
                .translationY(0F)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(totalDuration/2)
                .start()
        }
        .start()
}

fun TextView.adaptStyleToState(name: String) {

    if (tag == "highlight") {
        setChipHighlighted(name)
    } else {
        setChipDefault(name)
    }
}


fun TextView.setChipHighlighted(name: String) {

    animateChange {
        text = name
        this.setTextColor(resources.getColor(R.color.chip_highlight))
        setBackgroundResource(R.drawable.chip_highlighted)
        setPadding(
            dpToPx(10F),
            dpToPx(8F),
            dpToPx(10F),
            dpToPx(8F)
        )
    }

}

fun TextView.setChipDefault(name: String, animate: Boolean = true) {
    if (animate) {
        animateChange {
            text = name
            this@setChipDefault.setTextColor(resources.getColor(R.color.chip_text_default))
            this@setChipDefault.setBackgroundResource(R.drawable.chip_default)
            setPadding(
                dpToPx(10F),
                dpToPx(8F),
                dpToPx(10F),
                dpToPx(8F)
            )
        }

    } else {
        text = name
        this@setChipDefault.setTextColor(resources.getColor(R.color.chip_text_default))
        this@setChipDefault.setBackgroundResource(R.drawable.chip_default)
        setPadding(
            dpToPx(10F),
            dpToPx(8F),
            dpToPx(10F),
            dpToPx(8F)
        )
    }


}


/**
 * Chip animation where the view shrinks to zero and reappears with a different style.
 * Also has bounce.
 */
fun TextView.animateChange(totalDuration: Long = 200, fromScaleX: Float = 1F, fromScaleY: Float = 1F, toScaleX: Float = 0f, toScaleY: Float = 0f, applyChanges: () -> Unit = {}) {


    val animIn = ScaleAnimation(
        fromScaleX,
        toScaleX,
        fromScaleY,
        toScaleY,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )
    animIn.duration = totalDuration/2
//    animIn.setInterpolator(this.context, android.R.interpolator.accelerate_quint)
    this.startAnimation(animIn)

    animIn.setAnimationListener(object : Animation.AnimationListener {

        override fun onAnimationEnd(p0: Animation?) {

            applyChanges()

            val animOut =
                ScaleAnimation(
                    toScaleX,
                    fromScaleX,
                    toScaleY,
                    fromScaleY,
                    Animation.RELATIVE_TO_SELF,
                    0.5F,
                    Animation.RELATIVE_TO_SELF,
                    0.5F
                )
            animOut.setInterpolator(this@animateChange.context, android.R.interpolator.overshoot)
            animOut.duration = totalDuration/2
            this@animateChange.startAnimation(animOut)
        }

        override fun onAnimationRepeat(p0: Animation?) {}
        override fun onAnimationStart(p0: Animation?) {}
    })

}