package ru.teamdroid.colibripost.ui.main.calendar

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.ItemCalendarBinding

class AnimatorHelper(val binding: ItemCalendarBinding, val week: Week) :
        Transition.TransitionListener {
    private val set = ConstraintSet()
    private val transition = AutoTransition()
    private var endTransitionTextColor = Color.BLACK
    private var selectedDayIndex = 1
    private var startTransitionTextColor = Color.BLACK
    private var previousSelectedDayIndex = 1
    private val animationDuration = 300L

    init {
        transition.duration = animationDuration
        transition.addListener(this)
    }

    override fun onTransitionEnd(transition: Transition) {
        changeSelectedDayTextColor()
    }

    override fun onTransitionResume(transition: Transition) {
    }

    override fun onTransitionPause(transition: Transition) {
    }

    override fun onTransitionCancel(transition: Transition) {
        changeSelectedDayTextColor()
    }

    override fun onTransitionStart(transition: Transition) {
        changePreviousSelectedDayTextColor()
    }


    fun animateChangeSelection(selectedDay: Day, previousSelectedDay: Day) {

        if (week.containsDay(selectedDay) && week.containsDay(previousSelectedDay)) {
            val positionOfDay = week.getPositionOfDay(previousSelectedDay)
            previousSelectedDayIndex = positionOfDay
            startTransitionTextColor = colorRes(binding.container.context, R.color.text)
            animateFromStartToEnd(selectedDay)
        } else {
            setupStartAnimationState(previousSelectedDay)
            binding.container.doOnPreDraw {
                animateFromStartToEnd(selectedDay)
            }
        }
    }

    private fun animateFromStartToEnd(selectedDay: Day) {
        set.clone(binding.container)
        val positionOfDay = week.getPositionOfDay(selectedDay)
        selectedDayIndex = positionOfDay
        endTransitionTextColor = Color.WHITE
        setupViewPosition(viewIdSelectedDay(positionOfDay))
        TransitionManager.beginDelayedTransition(binding.container, transition)
        set.applyTo(binding.container)
    }

    private fun setupStartAnimationState(previousSelectedDay: Day) {
        set.clone(binding.container)
        when (val day = week.getPositionOfDay(previousSelectedDay)) {
            Week.DAY_BEFORE_THIS_WEEK -> setupViewOutsidePosition(true)
            Week.DAY_AFTER_THIS_WEEK -> setupViewOutsidePosition(false)
            else -> setupViewPosition(viewIdSelectedDay(day))
        }
        set.applyTo(binding.container)
    }

    private fun setupViewPosition(viewId: Int) {
        set.clear(binding.viewSelectedDay.id, ConstraintSet.START)
        set.clear(binding.viewSelectedDay.id, ConstraintSet.END)
        set.clear(binding.viewSelectedDay.id, ConstraintSet.LEFT)
        set.clear(binding.viewSelectedDay.id, ConstraintSet.RIGHT)
        set.connect(binding.viewSelectedDay.id, ConstraintSet.START, viewId, ConstraintSet.START)
        set.connect(binding.viewSelectedDay.id, ConstraintSet.END, viewId, ConstraintSet.END)
    }

    private fun setupViewOutsidePosition(isOutLeftSide: Boolean) {
        set.clear(binding.viewSelectedDay.id, ConstraintSet.START)
        set.clear(binding.viewSelectedDay.id, ConstraintSet.END)
        set.clear(binding.viewSelectedDay.id, ConstraintSet.LEFT)
        set.clear(binding.viewSelectedDay.id, ConstraintSet.RIGHT)
        if (isOutLeftSide) {
            set.connect(
                    binding.viewSelectedDay.id,
                    ConstraintSet.END,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START
            )
        } else {
            set.connect(
                    binding.viewSelectedDay.id,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.END
            )
        }
    }

    private fun changeTextColor(day: Int, color: Int) {
        when (day) {
            1 -> binding.tvNumberFirst.setTextColor(color)
            2 -> binding.tvNumberSecond.setTextColor(color)
            3 -> binding.tvNumberThird.setTextColor(color)
            4 -> binding.tvNumberFourth.setTextColor(color)
            5 -> binding.tvNumberFifth.setTextColor(color)
            6 -> binding.tvNumberSixth.setTextColor(color)
            7 -> binding.tvNumberSeventh.setTextColor(color)
        }
    }

    private fun changeSelectedDayTextColor() {
        when (selectedDayIndex) {
            1 -> binding.tvNumberFirst.setTextColor(endTransitionTextColor)
            2 -> binding.tvNumberSecond.setTextColor(endTransitionTextColor)
            3 -> binding.tvNumberThird.setTextColor(endTransitionTextColor)
            4 -> binding.tvNumberFourth.setTextColor(endTransitionTextColor)
            5 -> binding.tvNumberFifth.setTextColor(endTransitionTextColor)
            6 -> binding.tvNumberSixth.setTextColor(endTransitionTextColor)
            7 -> binding.tvNumberSeventh.setTextColor(endTransitionTextColor)
        }
    }

    private fun changePreviousSelectedDayTextColor() {
        when (previousSelectedDayIndex) {
            1 -> binding.tvNumberFirst.setTextColor(startTransitionTextColor)
            2 -> binding.tvNumberSecond.setTextColor(startTransitionTextColor)
            3 -> binding.tvNumberThird.setTextColor(startTransitionTextColor)
            4 -> binding.tvNumberFourth.setTextColor(startTransitionTextColor)
            5 -> binding.tvNumberFifth.setTextColor(startTransitionTextColor)
            6 -> binding.tvNumberSixth.setTextColor(startTransitionTextColor)
            7 -> binding.tvNumberSeventh.setTextColor(startTransitionTextColor)
        }
    }


    private fun viewIdSelectedDay(day: Int): Int {
        return when (day) {
            1 -> binding.tvNumberFirst.id
            2 -> binding.tvNumberSecond.id
            3 -> binding.tvNumberThird.id
            4 -> binding.tvNumberFourth.id
            5 -> binding.tvNumberFifth.id
            6 -> binding.tvNumberSixth.id
            7 -> binding.tvNumberSeventh.id
            else -> throw IllegalStateException("idSelectedDay")
        }
    }

    private fun colorRes(context: Context, @ColorRes color: Int): Int {
        return ContextCompat.getColor(context, color)
    }


}