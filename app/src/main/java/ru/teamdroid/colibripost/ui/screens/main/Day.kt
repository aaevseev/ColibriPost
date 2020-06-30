package ru.teamdroid.colibripost.ui.screens.main

import java.util.Calendar.*

class Day(var time: Long) {
    var dayOfMonth: Int = 1
    var dayOfWeek: Int = 1
    var month = 1
    var year: Int = 2020

    var delayedPost = DelayedPosts.NONE


    init {
        val cal = getInstance()
        cal.timeInMillis = time
        dayOfMonth = cal[DAY_OF_MONTH]
        dayOfWeek = cal[DAY_OF_WEEK]
        month = cal[MONTH] + 1
        year = cal[YEAR]
    }

    operator fun compareTo(other: Day): Int {
        if (this.year > other.year) return 1
        if (this.year < other.year) return -1
        if (this.month > other.month) return 1
        if (this.month < other.month) return -1
        if (this.dayOfMonth > other.dayOfMonth) return 1
        if (this.dayOfMonth < other.dayOfMonth) return -1
        return 0
    }

    override fun toString(): String {
        return "Day (year=$year, month=$month, dayOfMonth=$dayOfMonth)"
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Day

        if (dayOfMonth != other.dayOfMonth) return false
        if (dayOfWeek != other.dayOfWeek) return false
        if (month != other.month) return false
        if (year != other.year) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dayOfMonth
        result = 31 * result + dayOfWeek
        result = 31 * result + month
        result = 31 * result + year
        return result
    }

    enum class DelayedPosts {
        NONE, DELAYED, ERROR
    }

}