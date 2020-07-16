package ru.teamdroid.colibripost.ui.screens.new_task

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import ru.teamdroid.colibripost.R


class NewTaskSpinner : AppCompatSpinner {
    private var openInitiated = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
    }


    override fun performClick(): Boolean {
        openInitiated = true
        onSpinnerStateChanged(openInitiated)
        return super.performClick()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasBeenOpened() && hasFocus) {
            performClosedEvent()
        }
    }

    private fun onSpinnerStateChanged(isOpened: Boolean) {
        val constraintLayout = this.selectedView as? ConstraintLayout
        val imageView: ImageView = if (constraintLayout != null) {
            constraintLayout.findViewById(R.id.iv_drop_arrow)
        } else {
            return
        }
        startArrowAnimation(imageView, isOpened)
        changeViewBackground(constraintLayout, isOpened)
    }

    private fun changeViewBackground(constraintLayout: ConstraintLayout, opened: Boolean) {
        constraintLayout.background =
            if (opened) context.getDrawable(R.drawable.new_post_preview_button_background)
            else context.getDrawable(R.drawable.new_post_spinner_background)
    }

    private fun startArrowAnimation(imageView: ImageView, opened: Boolean) {
        val rotationStart = if (opened) 0f else 180f
        val rotationEnd = if (opened) 180f else 360f
        val animator =
            ObjectAnimator.ofFloat(imageView, View.ROTATION, rotationStart, rotationEnd)
        animator.duration = 250
        animator.addListener(onEnd = {
            if (!opened) {
                imageView.rotation = 0f
            }
        })
        animator.start()

    }

    fun performClosedEvent() {
        openInitiated = false
        onSpinnerStateChanged(openInitiated)
    }

    fun hasBeenOpened(): Boolean {
        return openInitiated
    }

}