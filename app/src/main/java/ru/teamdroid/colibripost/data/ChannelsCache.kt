package ru.teamdroid.colibripost.data

import ru.teamdroid.colibripost.domain.channels.ChannelEntity

interface ChannelsCache {

    fun saveChannel(entity: ChannelEntity)

    fun getChannel(key: Long): ChannelEntity?

    fun getChannels(): List<ChannelEntity>

    fun removeChannelEntity(key: Long)

}