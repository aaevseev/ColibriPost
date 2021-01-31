package ru.teamdroid.colibripost.data.post

import android.content.Context
import kotlinx.coroutines.delay
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.domain.post.PostRepository
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.domain.type.onNext
import ru.teamdroid.colibripost.remote.core.NetworkHandler
import ru.teamdroid.colibripost.remote.post.PostUtils

class PostRepositoryImpl(
        private val postCache: PostCache,
        private val postRemote:PostRemote,
        private val networkHandler: NetworkHandler,
        private val context: Context
): PostRepository{

    override suspend fun getChatSchedulesMessages(chatIds: List<Long>, scheduleDay:Long, day:Int, month:Int, year:Int, channelsIds:List<Long>):
            Either<Failure, List<PostEntity>> {

        val posts: List<PostEntity> = if(networkHandler.isConnected != null) postRemote.getChatScheduledMessages(chatIds, scheduleDay, day, month, year, channelsIds)
                                     else postCache.getPostsByDay(day, month, year)
        return when(posts.size){
            0 -> Either.Left(Failure.PostsListIsEmptyError)
            else -> {
                posts.map { postCache.savePost(it) }
                val filteredPosts = PostUtils.filterMediaAlbumPosts(posts)
                PostUtils.setImageText(filteredPosts, context)
                return Either.Right(filteredPosts)
            }
        }
    }

    override suspend fun checkPostsOnWeek(chatIds: List<Long>, times:List<Long>): Either<Failure, List<Boolean>> {
        val existList = mutableListOf<Boolean>()
        existList.addAll(postRemote.checkChatScheduledMessagesOnWeek(chatIds, times))
        return Either.Right(existList)
    }

    override suspend fun deleteSchedulePost(post:PostEntity): Either<Failure, None> {
        val messagesIds = mutableListOf(post.id)
        if(post.mediaAlbumId != 0L){
            messagesIds.addAll(postCache.getRelatedPosts(post.id, post.mediaAlbumId).map { it.id })
        }
        postRemote.deleteSchedulePost(post.chatId, messagesIds.toLongArray())
        return Either.Right(None())
    }

    override suspend fun duplicateSchedulePost(posts: List<PostEntity>): Either<Failure, None> {
        postRemote.duplicateSchedulePost(posts)
        return Either.Right(None())
    }

    override suspend fun getScheduledAlbumPost(post:PostEntity): Either<Failure, List<PostEntity>> {
        val posts = mutableListOf<PostEntity>()
        posts.add(post)
        if(post.mediaAlbumId != 0L) posts.addAll(postCache.getRelatedPosts(post.id, post.mediaAlbumId))
        return Either.Right(posts)
    }
}