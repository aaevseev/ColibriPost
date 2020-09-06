package ru.teamdroid.colibripost.data

import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.channels.ChannelsRepository
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.onNext
import ru.teamdroid.colibripost.remote.core.NetworkHandler

class ChannelsRepositoryImpl(
    private val channelsRemote: ChannelsRemote,
    private val channelsCache: ChannelsCache,
    private val networkHandler: NetworkHandler
) : ChannelsRepository{

    suspend override fun getChannels(): Either<Failure, List<ChannelEntity>> {
        /*val actualChannels = channelsCache.getChannels()
        return  when (actualChannels.size) {
            0 -> Either.Left(Failure.ChannelsListIsEmptyError)
            else -> {*/
                return if (networkHandler.isConnected!!) Either.Right(channelsRemote.getChannels(
                    listOf(1, 2, 3)))
                                .onNext { it.map { channelsCache.saveChannel(it) } }
                else Either.Left(Failure.NetworkConnectionError)
          //  }
        //}
    }
}