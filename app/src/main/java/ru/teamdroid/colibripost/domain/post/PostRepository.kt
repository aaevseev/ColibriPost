package ru.teamdroid.colibripost.domain.post

import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure

interface PostRepository {

    suspend fun getChatSchedulesMessages(chatIds: List<Long>, scheduleDay:Long): Either<Failure, List<PostEntity>>
}