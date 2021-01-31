package ru.teamdroid.colibripost.domain.post

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.type.None
import javax.inject.Inject

class DuplicatePost @Inject constructor(
        private val postRepository: PostRepository
) : UseCase<None, DuplicatePost.Params>() {


    data class Params(val posts:List<PostEntity>)

    override suspend fun run(params: Params) = postRepository.duplicateSchedulePost(params.posts)
}