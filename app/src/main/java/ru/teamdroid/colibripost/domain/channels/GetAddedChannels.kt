package ru.teamdroid.colibripost.domain.channels

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.type.None
import javax.inject.Inject

class GetAddedChannels @Inject constructor(
    private val channelsRepository: ChannelsRepository
) : UseCase<List<ChannelEntity>, None>() {

    override suspend fun run(params: None) = channelsRepository.getAddedChannels()

}