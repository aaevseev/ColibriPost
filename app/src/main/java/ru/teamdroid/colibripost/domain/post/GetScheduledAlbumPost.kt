package ru.teamdroid.colibripost.domain.post

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import javax.inject.Inject

class GetScheduledAlbumPost @Inject constructor(
        private val postRepository: PostRepository
) : UseCase<List<PostEntity>, GetScheduledAlbumPost.Params>()  {


    data class Params(val post:PostEntity)

    override suspend fun run(params: Params) = postRepository.getScheduledAlbumPost(params.post)

}