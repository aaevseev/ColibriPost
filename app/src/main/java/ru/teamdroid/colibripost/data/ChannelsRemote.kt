package ru.teamdroid.colibripost.data

import ru.teamdroid.colibripost.domain.channels.ChannelEntity

interface ChannelsRemote {

    suspend fun getAddedChannels(chatIds:List<Long>):List<ChannelEntity>

    suspend fun getAvChannels(chatIds:List<Long>):List<ChannelEntity>

}