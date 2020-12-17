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

    suspend fun getSchedulePosts(chatIds: List<Long>, calendarDay:Long): List<PostEntity>{

        val posts = mutableListOf<PostEntity>()

        chatIds.map { id ->
            getChatScheduleMessages(id).messages
        }.map {
            val messages = mutableListOf<PostEntity>()
            it.forEach {
                val date = Date((it.schedulingState as TdApi.MessageSchedulingStateSendAtDate).sendDate * 1000L)
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

    fun scheduleDateCompare(date: Date, selectedDate: Date): Boolean{
        val day = getDay(date)
        val month = getMonth(date)
        val year = getYear(date)
        val selectedDay = getDay(selectedDate)
        val selectedMonth = getMonth(selectedDate)
        val selectedYear = getYear(selectedDate)
        return day.equals(selectedDay) && month.equals(selectedMonth) && year.equals(selectedYear)
    }


    fun getDay(scheduleDay: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return calendar.get(Calendar.DAY_OF_MONTH).toString()
    }

    fun getMonth(scheduleDay: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return calendar.get(Calendar.MONTH).toString()
    }

    fun getYear(scheduleDay: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return calendar.get(Calendar.YEAR).toString()
    }

    suspend fun getChatScheduleMessages(id:Long):TdApi.Messages{
        return client.send<TdApi.Messages>(TdApi.GetChatScheduledMessages(id))
    }

}