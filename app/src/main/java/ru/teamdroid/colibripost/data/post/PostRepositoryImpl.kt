package ru.teamdroid.colibripost.data.post

import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.domain.post.PostRepository
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.remote.core.NetworkHandler

class PostRepositoryImpl(
        private val postRemote:PostRemote,
        private val networkHandler: NetworkHandler
): PostRepository {

    override suspend fun getChatSchedulesMessages(chatIds: List<Long>):
            Either<Failure, List<PostEntity>> {
        return Either.Right(postRemote.getChatScheduledMessages(chatIds))
    }
}