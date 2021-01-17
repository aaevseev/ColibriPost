package ru.teamdroid.colibripost.remote.post

import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.remote.core.TelegramClient
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRequests @Inject constructor(
        private val client:TelegramClient
){

    suspend fun getSchedulePosts(chatIds: List<Long>, calendarDay:Long, day:Int, month:Int, year:Int): List<PostEntity>{

        val posts = mutableListOf<PostEntity>()

        chatIds.map { id ->
            getChatScheduleMessages(id).messages
        }.map {
            val messages = mutableListOf<PostEntity>()
            it.forEach {
                val date = Date(convertSendDateToLong(it))
                if(scheduleDateCompare(date, Date(calendarDay))){
                    val post = PostEntity()
                    post.fill(it)
                    posts.add(post)
                }
            }
            posts.addAll(messages)
        }
        return posts
    }

    suspend fun getWeekScheduleInfo(chatIds: List<Long>, calendarDays:List<Long>): List<Boolean>{

        val posts = mutableListOf<PostEntity>()

        chatIds.map { id ->
            getChatScheduleMessages(id).messages
        }
                .map {
            val messages = mutableListOf<PostEntity>()
            it.forEach {
                val date = Date(convertSendDateToLong(it))
                if(scheduleWeekDateCompare(date, calendarDays)){
                    val post = PostEntity()
                    post.fill(it)
                    posts.add(post)
                }
            }
            posts.addAll(messages)
        }
        return getExistingInfo(calendarDays, posts)
    }

    suspend fun getChatScheduleMessages(id:Long):TdApi.Messages{
        return client.send(TdApi.GetChatScheduledMessages(id))
    }

    //region Utils

    fun scheduleWeekDateCompare(date: Date, selectedWeek:List<Long>): Boolean{

        val datesOfWeek = selectedWeek.map { Date(it) }

        val day = getDay(date)
        val month = getMonth(date)
        val year = getYear(date)

        var selectedDay = ""
        var selectedMonth = ""
        var selectedYear = ""

        var compareIndicate = false

        for(scheduleDay: Date in datesOfWeek){
            selectedDay = getDay(scheduleDay)
            selectedMonth = getMonth(scheduleDay)
            selectedYear = getYear(scheduleDay)

            if(day.equals(selectedDay) && month.equals(selectedMonth) && year.equals(selectedYear)) {
                compareIndicate = true
                break
            }
        }
        return compareIndicate
    }

    fun scheduleDateCompare(date: Date, selectedDate: Date): Boolean{
        val day = getDay(date)
        val month = getMonth(date)
        val year = getYear(date)
        val selectedDay = getDay(selectedDate)
        val selectedMonth = getMonth(selectedDate)
        val selectedYear = getYear(selectedDate)
        return day.equals(selectedDay) && month.equals(selectedMonth) && year.equals(selectedYear)
    }

    fun getExistingInfo(calendarDays: List<Long>, posts:MutableList<PostEntity>):List<Boolean>{
        val existList = mutableListOf<Boolean>()
        for(i:Int in 0..6){
            if(posts.firstOrNull {
                        scheduleDateCompare(Date(it.scheduleDate * 1000L), Date(calendarDays.get(i)))
            } != null) existList.add(true)
            else existList.add(false)
        }
        return existList
    }

    fun getDay(scheduleDay: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return calendar.get(Calendar.DAY_OF_MONTH).toString()
    }

    fun getMonth(scheduleDay: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return (calendar.get(Calendar.MONTH)+1).toString()
    }

    fun getYear(scheduleDay: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return calendar.get(Calendar.YEAR).toString()
    }

    fun convertSendDateToLong(message: TdApi.Message):Long{
        return (message.schedulingState as TdApi.MessageSchedulingStateSendAtDate).sendDate * 1000L
    }

    //endregion

}