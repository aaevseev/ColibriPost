package ru.teamdroid.colibripost.data.post

import ru.teamdroid.colibripost.domain.post.PostEntity

interface PostRemote {
    suspend fun getChatScheduledMessages(chatIds: List<Long>, calendarDay:Long, day:Int, month:Int, year:Int):List<PostEntity>
    suspend fun checkChatScheduledMessagesOnWeek(chatIds: List<Long>, times:List<Long>):List<Boolean>
}