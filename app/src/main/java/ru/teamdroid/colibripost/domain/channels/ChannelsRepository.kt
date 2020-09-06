package ru.teamdroid.colibripost.domain.channels

import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure

interface ChannelsRepository {

    suspend fun getChannels(): Either<Failure, List<ChannelEntity>>

}