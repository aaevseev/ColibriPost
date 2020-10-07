package ru.teamdroid.colibripost.remote.channels

import ru.teamdroid.colibripost.data.channels.ChannelsRemote
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import javax.inject.Inject

class ChannelsRemoteImpl @Inject constructor(
    private val chatsRequests: ChatsRequests
): ChannelsRemote {


    override suspend fun getAddedChannels(chatIds:List<Long>): List<ChannelEntity> {
        return chatsRequests.getChannelsFullInfo(chatIds, true)
    }

    override suspend fun getAvailableChannels(chatIds:List<Long>): List<ChannelEntity> {
        return chatsRequests.getChannelsFullInfo(chatIds, false)
    }
}