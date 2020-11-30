package ru.teamdroid.colibripost.remote.post

import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.remote.core.TelegramClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRequests @Inject constructor(
        private val client:TelegramClient
){

    suspend fun getSchedulePosts(chatIds: List<Long>): List<PostEntity>{

        val posts = mutableListOf<PostEntity>()

        chatIds.map { id ->
            getChatScheduleMessages(id).messages
        }.map {
            val messages = mutableListOf<PostEntity>()
            it.forEach {
                val post = PostEntity()
                post.fill(it)
                posts.add(post)
            }
            posts.addAll(messages)
        }

        return posts

    }

    suspend fun getChatScheduleMessages(id:Long):TdApi.Messages{
        return client.send<TdApi.Messages>(TdApi.GetChatScheduledMessages(id))
    }

}