package ru.teamdroid.colibripost.data

import ru.teamdroid.colibripost.domain.channels.ChannelEntity

interface ChannelsRemote {

    suspend fun getChannels(chatIds:List<Long>):List<ChannelEntity>

}