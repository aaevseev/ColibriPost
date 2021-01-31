package ru.teamdroid.colibripost.domain.post

import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None

interface PostRepository {

    suspend fun getChatSchedulesMessages(chatIds: List<Long>, scheduleDay:Long, day:Int, month:Int, year:Int, channelsIds:List<Long>): Either<Failure, List<PostEntity>>

    suspend fun checkPostsOnWeek(chatIds: List<Long>, times:List<Long>): Either<Failure, List<Boolean>>

    suspend fun deleteSchedulePost(post:PostEntity):Either<Failure, None>

    suspend fun duplicateSchedulePost(post:List<PostEntity>):Either<Failure, None>

    suspend fun getScheduledAlbumPost(post:PostEntity): Either<Failure, List<PostEntity>>
}