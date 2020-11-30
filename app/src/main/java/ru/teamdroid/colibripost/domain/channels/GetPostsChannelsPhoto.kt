package ru.teamdroid.colibripost.domain.channels

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.post.PostEntity
import javax.inject.Inject

class GetPostsChannelsPhoto @Inject constructor(
        private val channelsRepository: ChannelsRepository
) : UseCase<List<PostEntity>, List<PostEntity>>() {

    override suspend fun run(params: List<PostEntity>) =
            channelsRepository.getPostsChannelPhoto(params)
}