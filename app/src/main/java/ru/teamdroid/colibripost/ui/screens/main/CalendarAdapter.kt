package ru.teamdroid.colibripost.ui.screens.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.ItemCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.WeekHolder>() {
    private val weeks =
        Week.getWeekAround(5) //нужно ли делать бесконечный скролл или мы будем отображать посты в определённом периоде?

    var selectedDay = Day(System.currentTimeMillis())
        private set(value) {
            previousSelectedDay = field
            field = value
            refreshSelectedDays()
            calendarClickListener?.onDaySelected(value)
        }
    private var previousSelectedDay = selectedDay
    var calendarClickListener: CalendarClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCalendarBinding.inflate(inflater, parent, false)
        return WeekHolder(binding)
    }


    override fun onBindViewHolder(holder: WeekHolder, position: Int) {
        holder.bind(weeks[position])
    }

    override fun getItemCount(): Int {
        return weeks.size
    }


    fun getWeek(position: Int): Week {
        return weeks[position]
    }

    fun getPageOfDay(day: Day): Int {
        weeks.forEachIndexed { index, week ->
            if (week.containsDay(day)) {
                return index
            }
        }
        throw java.lang.IllegalStateException("day not in adapter")
    }

    fun selectDay(day: Day) {
        selectedDay = day
        refreshSelectedDays()
        notifyItemChanged(getPageOfDay(day))
    }

    //предварительный метод установки постов
    suspend fun setData(posts: List<Post>) = withContext(Dispatchers.Default) {
        val list = mutableListOf<Day>()
        posts.forEach { post ->
            val day = Day(post.time)
            day.delayedPost = if (post.success) {
                Day.DelayedPosts.DELAYED
            } else {
                Day.DelayedPosts.ERROR
            }
            list.add(day)
        }
        for (week in weeks) {
            for (day in list) {
                if (week.containsDay(day)) {
                    val pos = week.getPositionOfDay(day)
                    week.weekDays[pos] = day
                }
            }
        }
        withContext(Dispatchers.Main) {
            notifyDataSetChanged()
        }

    }

    private fun refreshSelectedDays() {
        var previousSelectedWeek = 0
        var selectedWeek = 0
        weeks.forEachIndexed { index, week ->
            if (week.containsDay(previousSelectedDay)) {
                previousSelectedWeek = index
            }
            if (week.containsDay(selectedDay)) {
                selectedWeek = index
            }
        }
        if (selectedWeek != previousSelectedWeek) {
            notifyItemChanged(previousSelectedWeek)
        }
    }


    inner class WeekHolder(private val binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val calendar = Calendar.getInstance()
        private val set = ConstraintSet()
        var animatorHelper: AnimatorHelper? = null

        fun bind(week: Week) = with(binding) {
            tvNumberFirst.text = week.getNumberOfMonth(1).toString()
            tvNumberSecond.text = week.getNumberOfMonth(2).toString()
            tvNumberThird.text = week.getNumberOfMonth(3).toString()
            tvNumberFourth.text = week.getNumberOfMonth(4).toString()
            tvNumberFifth.text = week.getNumberOfMonth(5).toString()
            tvNumberSixth.text = week.getNumberOfMonth(6).toString()
            tvNumberSeventh.text = week.getNumberOfMonth(7).toString()

            tvFirstDayWeek.text = getWeekDay(1)
            tvSecondDayWeek.text = getWeekDay(2)
            tvThirdDayWeek.text = getWeekDay(3)
            tvForthDayWeek.text = getWeekDay(4)
            tvFifthDayWeek.text = getWeekDay(5)
            tvSixthDayWeek.text = getWeekDay(6)
            tvSeventhDayWeek.text = getWeekDay(7)

            tvNumberFirst.setOnClickListener { onClick(week, 1) }
            tvNumberSecond.setOnClickListener { onClick(week, 2) }
            tvNumberThird.setOnClickListener { onClick(week, 3) }
            tvNumberFourth.setOnClickListener { onClick(week, 4) }
            tvNumberFifth.setOnClickListener { onClick(week, 5) }
            tvNumberSixth.setOnClickListener { onClick(week, 6) }
            tvNumberSeventh.setOnClickListener { onClick(week, 7) }

            tvFirstDayWeek.setOnClickListener { onClick(week, 1) }
            tvSecondDayWeek.setOnClickListener { onClick(week, 2) }
            tvThirdDayWeek.setOnClickListener { onClick(week, 3) }
            tvForthDayWeek.setOnClickListener { onClick(week, 4) }
            tvFifthDayWeek.setOnClickListener { onClick(week, 5) }
            tvSixthDayWeek.setOnClickListener { onClick(week, 6) }
            tvSeventhDayWeek.setOnClickListener { onClick(week, 7) }

            tvNumberFirst.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.textColor
                )
            )
            tvNumberSecond.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.textColor
                )
            )
            tvNumberThird.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.textColor
                )
            )
            tvNumberFourth.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.textColor
                )
            )
            tvNumberFifth.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.textColor
                )
            )
            tvNumberSixth.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.textColor
                )
            )
            tvNumberSeventh.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.textColor
                )
            )

            setupStateUnderView(viewUnderDay1, week.dayOfWeek(1))
            setupStateUnderView(viewUnderDay2, week.dayOfWeek(2))
            setupStateUnderView(viewUnderDay3, week.dayOfWeek(3))
            setupStateUnderView(viewUnderDay4, week.dayOfWeek(4))
            setupStateUnderView(viewUnderDay5, week.dayOfWeek(5))
            setupStateUnderView(viewUnderDay6, week.dayOfWeek(6))
            setupStateUnderView(viewUnderDay7, week.dayOfWeek(7))

            setupSelection(week)
            animatorHelper = AnimatorHelper(this, week)
        }

        private fun setupStateUnderView(view: View, day: Day) {
            when (day.delayedPost) {
                Day.DelayedPosts.NONE -> view.backgroundTintList =
                    ColorStateList.valueOf(Color.TRANSPARENT)
                Day.DelayedPosts.DELAYED -> view.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(view.context, R.color.accent))
                Day.DelayedPosts.ERROR -> view.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(view.context, R.color.red))
            }
        }

        private fun setupSelection(week: Week) {
            if (week.containsDay(selectedDay)) {
                val positionOfDay = week.getPositionOfDay(selectedDay)
                changeConstraintSelection(positionOfDay)
            } else {
                removeDaySelection()
            }
        }

        private fun removeDaySelection() {
            set.clone(binding.container)
            set.clear(binding.viewSelectedDay.id, START)
            set.clear(binding.viewSelectedDay.id, END)
            set.clear(binding.viewSelectedDay.id, LEFT)
            set.clear(binding.viewSelectedDay.id, RIGHT)
            set.connect(binding.viewSelectedDay.id, END, PARENT_ID, START)
            set.applyTo(binding.container)
        }

        private fun idSelectedDay(day: Int): Int {
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


        private fun onClick(week: Week, dayWeek: Int) {
            selectedDay = week.dayOfWeek(dayWeek)
            animatorHelper?.animateChangeSelection(selectedDay, previousSelectedDay)
        }


        private fun changeConstraintSelection(position: Int) {
            set.clone(binding.container)
            setupViewPosition(idSelectedDay(position))
            set.applyTo(binding.container)
        }

        private fun setupViewPosition(viewId: Int) {
            set.clear(binding.viewSelectedDay.id, START)
            set.clear(binding.viewSelectedDay.id, END)
            set.clear(binding.viewSelectedDay.id, LEFT)
            set.clear(binding.viewSelectedDay.id, RIGHT)
            set.connect(binding.viewSelectedDay.id, START, viewId, START)
            set.connect(binding.viewSelectedDay.id, END, viewId, END)
        }

        private fun getWeekDay(day: Int): String {
            calendar.set(Calendar.DAY_OF_WEEK, day)
            return SimpleDateFormat("E", Locale.getDefault())
                .format(calendar.time).toLowerCase(Locale.getDefault())
        }

    }

}