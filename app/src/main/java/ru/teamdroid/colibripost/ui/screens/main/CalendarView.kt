package ru.teamdroid.colibripost.ui.screens.main

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import ru.teamdroid.colibripost.databinding.CalendarViewBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    var calendarClickListener: CalendarClickListener? = null
        set(value) {
            field = value
            adapter.calendarClickListener = value
        }
    var selectedDay = Day(System.currentTimeMillis())
        get() = adapter.selectedDay
        set(value) {
            field = value
            adapter.selectDay(value)
            binding.vpCalendar.setCurrentItem(adapter.getPageOfDay(value), false)
        }
    private val binding: CalendarViewBinding
    private val adapter = CalendarAdapter()


    init {
        //сделал отдельным вью, можно встроить календарь в фрагмент. Как лучше делать? Я считаю во вью лучше
        val inflater = LayoutInflater.from(this.context)
        binding = CalendarViewBinding.inflate(inflater, this, true)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        binding.vpCalendar.adapter = adapter
        selectedDay = Day(System.currentTimeMillis())
        setupNumbersOfWeek(Week(selectedDay.time))
        setupListeners()
    }

    suspend fun setPosts(posts: List<Post>) {
        adapter.setData(posts)
    }


    private fun setupListeners() {
        binding.vpCalendar.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setupNumbersOfWeek(adapter.getWeek(position))
            }
        })
        binding.imgBtnNextWeek.setOnClickListener {
            binding.vpCalendar.currentItem++
        }
        binding.imgBtnPreviousWeek.setOnClickListener {
            binding.vpCalendar.currentItem--
        }
    }

    private fun setupNumbersOfWeek(week: Week) {
        val monthFirstDayWeek = week.getMonthOfDay(1)
        val monthLastDayWeek = week.getMonthOfDay(7)
        val weekInOneMonth = monthFirstDayWeek == monthLastDayWeek
        val firstDay = if (weekInOneMonth) {
            SimpleDateFormat("d", Locale.getDefault()).format(week.dayOfWeek(1).time)
        } else {
            SimpleDateFormat("d MMMM", Locale.getDefault()).format(week.dayOfWeek(1).time)
        }
        val lastDay = SimpleDateFormat("d MMMM", Locale.getDefault()).format(week.dayOfWeek(7).time)
        val text = "$firstDay - $lastDay"
        binding.tvWeek.text = text
    }


}