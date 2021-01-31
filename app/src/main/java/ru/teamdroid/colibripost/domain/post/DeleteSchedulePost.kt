package ru.teamdroid.colibripost.domain.post

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import javax.inject.Inject

class DeleteSchedulePost @Inject constructor(
        private val postRepository: PostRepository
) : UseCase<None, DeleteSchedulePost.Params>() {

    data class Params(val post:PostEntity)

    override suspend fun run(params: Params): Either<Failure, None> = postRepository.deleteSchedulePost(params.post)

}