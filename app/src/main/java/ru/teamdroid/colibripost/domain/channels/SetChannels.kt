package ru.teamdroid.colibripost.domain.channels

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.type.None
import javax.inject.Inject

class SetChannels @Inject constructor(
    private val channelsRepository: ChannelsRepository
) : UseCase<None, List<ChannelEntity>>() {

    override suspend fun run(params: List<ChannelEntity>) =
        channelsRepository.setChannels(params)
}