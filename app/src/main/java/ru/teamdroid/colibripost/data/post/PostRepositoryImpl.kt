package ru.teamdroid.colibripost.data.post

import kotlinx.coroutines.delay
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.domain.post.PostRepository
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.onNext
import ru.teamdroid.colibripost.remote.core.NetworkHandler

class PostRepositoryImpl(
        private val postCache: PostCache,
        private val postRemote:PostRemote,
        private val networkHandler: NetworkHandler
): PostRepository{

    override suspend fun getChatSchedulesMessages(chatIds: List<Long>, scheduleDay:Long, day:Int, month:Int, year:Int):
            Either<Failure, List<PostEntity>> {

        val posts: List<PostEntity> = if(networkHandler.isConnected != null) postRemote.getChatScheduledMessages(chatIds, scheduleDay, day, month, year)
                                     else postCache.getPostsByDay(day, month, year)
        return when(posts.size){
            0 -> Either.Left(Failure.PostsListIsEmptyError)
            else -> {
                return  Either.Right(posts).onNext { it.map { postCache.savePost(it) } }
            }
        }
    }

    override suspend fun checkPostsOnWeek(chatIds: List<Long>, times:List<Long>): Either<Failure, List<Boolean>> {
        val existList = mutableListOf<Boolean>()
        existList.addAll(postRemote.checkChatScheduledMessagesOnWeek(chatIds, times))
        return Either.Right(existList)
    }
}