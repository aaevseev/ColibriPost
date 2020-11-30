package ru.teamdroid.colibripost.ui.main.calendar

import java.util.*

class Week(dayTime: Long) {
    private val calendar = Calendar.getInstance()
    val weekDays: MutableList<Day> = mutableListOf()


    companion object {
        private const val WEEK = 604800000L
        const val DAY_AFTER_THIS_WEEK = 8
        const val DAY_BEFORE_THIS_WEEK = 0

        fun getWeekAround(
            weekAround: Int,
            time: Long = System.currentTimeMillis()
        ): MutableList<Week> {
            var t = time - (weekAround * WEEK)
            val listOfWeeks = mutableListOf<Week>()
            val weeksNeed = (weekAround * 2) + 1
            for (i in 0 until weeksNeed) {
                listOfWeeks.add(
                    Week(
                        t
                    )
                )
                t += WEEK
            }
            return listOfWeeks
        }
    }

    init {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = getFirstDayOfWeek(dayTime)
        for (i in 0..6) {
            weekDays.add(
                Day(
                    calendar.timeInMillis
                )
            )
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
    }

    private fun getFirstDayOfWeek(time: Long): Long {
        calendar.timeInMillis = time
        while (calendar.get(Calendar.DAY_OF_WEEK) != calendar.firstDayOfWeek) {
            calendar.add(Calendar.DATE, -1);
        }
        return calendar.timeInMillis
    }

    fun dayOfWeek(day: Int): Day {
        return weekDays[day - 1]
    }

    fun getPositionOfDay(day: Day): Int {
        when {
            day < weekDays[0] -> return DAY_BEFORE_THIS_WEEK
            day > weekDays[6] -> return DAY_AFTER_THIS_WEEK
            else -> {
                weekDays.forEachIndexed { index, d ->
                    if (d == day) {
                        return index + 1
                    }
                }
                return DAY_BEFORE_THIS_WEEK
            }
        }
    }


    fun nextWeek(): Week {
        return Week(
            weekDays[0].time + WEEK
        )
    }

    fun previousWeek(): Week {
        return Week(
            weekDays[0].time - WEEK
        )
    }

    fun getMonthOfDay(dayWeek: Int): Int {
        return weekDays[dayWeek - 1].month
    }

    fun containsDay(dayTime: Day): Boolean {
        return weekDays.contains(dayTime)
    }


    fun getNumberOfMonth(dayWeek: Int): Int {
        return weekDays[dayWeek - 1].dayOfMonth
    }


    override fun toString(): String {
        return "Week(${weekDays[0].year}.${weekDays[0].month}.${weekDays[0].dayOfMonth} - ${weekDays[6].year}.${weekDays[6].month}.${weekDays[6].dayOfMonth})"
    }

}