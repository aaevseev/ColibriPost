package ru.teamdroid.colibripost.remote.post

import ru.teamdroid.colibripost.data.post.PostRemote
import ru.teamdroid.colibripost.domain.post.PostEntity
import javax.inject.Inject

class PostRemoteImpl @Inject constructor(
        private val postRequests: PostRequests
): PostRemote{
    override suspend fun getChatScheduledMessages(chatIds: List<Long>, calendarDay:Long, day:Int, month:Int, year:Int): List<PostEntity> {
        return postRequests.getSchedulePosts(chatIds, calendarDay, day, month, year)
    }

    override suspend fun checkChatScheduledMessagesOnWeek(chatIds: List<Long>, times: List<Long>): List<Boolean> {
        return postRequests.getWeekScheduleInfo(chatIds, times)
    }
}