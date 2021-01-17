package ru.teamdroid.colibripost.domain.post

import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure

interface PostRepository {

    suspend fun getChatSchedulesMessages(chatIds: List<Long>, scheduleDay:Long, day:Int, month:Int, year:Int): Either<Failure, List<PostEntity>>

    suspend fun checkPostsOnWeek(chatIds: List<Long>, times:List<Long>): Either<Failure, List<Boolean>>
}