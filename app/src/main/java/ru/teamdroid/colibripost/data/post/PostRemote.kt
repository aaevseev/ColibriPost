package ru.teamdroid.colibripost.data.post

import ru.teamdroid.colibripost.domain.post.PostEntity

interface PostRemote {
    suspend fun getChatScheduledMessages(chatIds: List<Long>):List<PostEntity>
}