package ru.teamdroid.colibripost.domain.channels

import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None

interface ChannelsRepository {

    suspend fun getAddedChannels(): Either<Failure, List<ChannelEntity>>

    suspend fun getAvailableChannels(): Either<Failure, List<ChannelEntity>>

    suspend fun setChannels(channels: List<ChannelEntity>): Either<Failure, None>

    suspend fun deleteChannel(idChannel: Long): Either<Failure, None>

}