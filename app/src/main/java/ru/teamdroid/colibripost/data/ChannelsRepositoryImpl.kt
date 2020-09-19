package ru.teamdroid.colibripost.data

import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.channels.ChannelsRepository
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.domain.type.onNext
import ru.teamdroid.colibripost.remote.core.NetworkHandler

class ChannelsRepositoryImpl(
    private val channelsRemote: ChannelsRemote,
    private val channelsCache: ChannelsCache,
    private val networkHandler: NetworkHandler
) : ChannelsRepository{

    suspend override fun getAddedChannels(): Either<Failure, List<ChannelEntity>> {
        val actualChannels = channelsCache.getChannels()
        return  when (actualChannels.size) {
            0 -> Either.Left(Failure.ChannelsListIsEmptyError)
            else -> {
                return if (networkHandler.isConnected != null) Either.Right(channelsRemote.getAddedChannels(
                    actualChannels.map { it.chatId }))
                                .onNext { it.map { channelsCache.saveChannel(it) } }
                else Either.Right(actualChannels)
            }
        }
    }

    override suspend fun getAvChannels(): Either<Failure, List<ChannelEntity>> {
        val actualChannels = channelsCache.getChannels()

        return if (networkHandler.isConnected != null){
            val avChannels = channelsRemote.getAvChannels(
                if (actualChannels.isNotEmpty()) actualChannels.map { it.chatId } else listOf())
            if(avChannels.isNotEmpty()) Either.Right(avChannels)
            else Either.Left(Failure.ChannelsNotCreatedError)
        }
        else Either.Left(Failure.NetworkPlaceHolderConnectionError)
    }

    override suspend fun setChannels(channels: List<ChannelEntity>): Either<Failure, None> {
        channelsCache.saveChannels(channels)
        return if(networkHandler.isConnected != null) {
            Either.Right(None())
        } else Either.Left(Failure.NetworkConnectionError)
    }

    override suspend fun deleteChannel(idChannel:Long): Either<Failure, None> {
        channelsCache.removeChannelEntity(idChannel)
        return if(networkHandler.isConnected != null) {
            Either.Right(None())
        } else Either.Left(Failure.NetworkConnectionError)
    }
}