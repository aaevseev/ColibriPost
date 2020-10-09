package ru.teamdroid.colibripost.data.channels

import ru.teamdroid.colibripost.domain.channels.ChannelEntity

interface ChannelsRemote {

    suspend fun getAddedChannels(chatIds: List<Long>): List<ChannelEntity>

    suspend fun getAvailableChannels(chatIds: List<Long>): List<ChannelEntity>

}