package ru.teamdroid.colibripost.domain.channels

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.type.None
import javax.inject.Inject

class DeleteChannel @Inject constructor(
    private val channelsRepository: ChannelsRepository
) : UseCase<None, Long>() {


    override suspend fun run(params: Long) = channelsRepository.deleteChannel(params)


}