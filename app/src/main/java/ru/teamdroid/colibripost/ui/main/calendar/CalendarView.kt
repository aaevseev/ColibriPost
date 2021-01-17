package ru.teamdroid.colibripost.ui.main.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.item_calendar.view.*
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.CalendarViewBinding
import ru.teamdroid.colibripost.ui.core.getColorState
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
    var selectedDay =
        Day(System.currentTimeMillis())
        get() = adapter.selectedDay
        set(value) {
            field = value
            adapter.selectDay(value)
            binding.vpCalendar.setCurrentItem(adapter.getPageOfDay(value), false)
        }


    val binding: CalendarViewBinding
    val adapter =
        CalendarAdapter()
    var currentWeekPosition:Int = 6



    init {
        //сделал отдельным вью, можно встроить календарь в фрагмент. Как лучше делать? Я считаю во вью лучше
        val inflater = LayoutInflater.from(this.context)
        binding = CalendarViewBinding.inflate(inflater, this, true)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        adapter.indicateEndOfList = {
            binding.imgBtnNextWeek.imageTintList = context.getColorState(R.color.accentEnabled)
        }
        adapter.indicateStartOfList = {
            binding.imgBtnPreviousWeek.imageTintList = context.getColorState(R.color.accentEnabled)
        }
        adapter.indicateMiddleOfList = {
            binding.imgBtnPreviousWeek.imageTintList = context.getColorState(R.color.accent)
            binding.imgBtnNextWeek.imageTintList = context.getColorState(R.color.accent)
        }
        binding.vpCalendar.adapter = adapter
        binding.vpCalendar.offscreenPageLimit = 1
        selectedDay =
            Day(System.currentTimeMillis())
        setupNumbersOfWeek(
            Week(
                selectedDay.time
            )
        )
        setupListeners()
    }

    suspend fun setPosts(posts: List<Post>) {
        adapter.setData(posts)
    }

    private fun setupListeners() {
        binding.vpCalendar.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d("ViewPagerCheck", "OnSelected " + position.toString())
                adapter.currentPosition = position
                setImageButtonState(position)
                setupNumbersOfWeek(adapter.getWeek(position))
            }
        })
        /*binding.imgBtnNextWeek.isEnabled = false
        binding.imgBtnPreviousWeek.isEnabled = false*/
        binding.imgBtnNextWeek.setOnClickListener {
            binding.vpCalendar.currentItem++
            Log.d("ViewPagerCheck","NextClick " + binding.vpCalendar.currentItem.toString())
        }
        binding.imgBtnPreviousWeek.setOnClickListener {
            binding.vpCalendar.currentItem--
            Log.d("ViewPagerCheck", "PreviousClick " + binding.vpCalendar.currentItem.toString())
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

    fun setImageButtonState(position: Int){
        when(position){
            adapter.weeks.size - 1 -> {
                binding.imgBtnNextWeek.imageTintList = context.getColorState(R.color.accentEnabled)
            }
            0 -> {
                binding.imgBtnPreviousWeek.imageTintList = context.getColorState(R.color.accentEnabled)
            }
            else -> {
                binding.imgBtnPreviousWeek.imageTintList = context.getColorState(R.color.accent)
                binding.imgBtnNextWeek.imageTintList = context.getColorState(R.color.accent)
            }
        }
    }

    /*private fun setUpDaysIndicator(week:Week, existingPostsOnWeek:List<Boolean>){
        Log.d("ViewPagerCheck", "SetUpDays")
        var i = 1
        for(isExist:Boolean in existingPostsOnWeek){
            week.dayOfWeek(i).delayedPost = if(isExist)  Day.DelayedPosts.DELAYED
            else Day.DelayedPosts.NONE
            i++
        }

        setupStateUnderView(view_under_day1, week.dayOfWeek(1))
        setupStateUnderView(view_under_day2, week.dayOfWeek(2))
        setupStateUnderView(view_under_day3, week.dayOfWeek(3))
        setupStateUnderView(view_under_day4, week.dayOfWeek(4))
        setupStateUnderView(view_under_day5, week.dayOfWeek(5))
        setupStateUnderView(view_under_day6, week.dayOfWeek(6))
        setupStateUnderView(view_under_day7, week.dayOfWeek(7))
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

    private fun getWeekTimes(week: Week):List<Long>{
        return listOf(week.dayOfWeek(1).time, week.dayOfWeek(2).time, week.dayOfWeek(3).time, week.dayOfWeek(4).time,
                week.dayOfWeek(5).time, week.dayOfWeek(6).time, week.dayOfWeek(7).time)
    }*/

}