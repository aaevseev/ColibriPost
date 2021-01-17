package ru.teamdroid.colibripost.domain.post

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import javax.inject.Inject

class CheckPostsOnWeek @Inject constructor(
        private val postRepository: PostRepository
) : UseCase<List<Boolean>, CheckPostsOnWeek.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<Boolean>> = postRepository.checkPostsOnWeek(params.chatIds, params.times)

    data class Params(val chatIds:List<Long>, val times:List<Long>)

}