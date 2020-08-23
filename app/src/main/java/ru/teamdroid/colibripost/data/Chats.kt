package ru.teamdroid.colibripost.data

import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.domain.ChannelEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Chats @Inject constructor(private val client: TelegramClient) {

    private suspend fun getChatIds(): LongArray {
        val getChats = TdApi.GetChats(TdApi.ChatListMain(), Long.MAX_VALUE, 0, 50)
        val chats = client.send<TdApi.Chats>(getChats)
        return chats.chatIds
    }

    private suspend fun getChannelsIds(): IntArray {
        val getChannels = TdApi.GetChannels(Long.MAX_VALUE, 0, 50)
        val channels = client.send<TdApi.Channels>(getChannels)
        return channels.channelIds
    }

    suspend fun getChats(): List<TdApi.Chat> = getChatIds()
        .map { ids -> getChat(ids) }

    suspend fun getChat(chatId: Long): TdApi.Chat {
        return client.send(TdApi.GetChat(chatId))
    }

    suspend fun getSupergroup(superGroupId: Int): TdApi.Supergroup {
        return client.send(TdApi.GetSupergroup(superGroupId))
    }

    suspend fun getSupergroupFullInfo(superGroupId: Int): TdApi.SupergroupFullInfo {
        return client.send(TdApi.GetSupergroupFullInfo(superGroupId))
    }
    
    suspend fun getAdminChannelSupergroups(): List<TdApi.Supergroup>{
        return getChats().filterByChannel()
            .map {chat -> getSupergroup((chat.type as TdApi.ChatTypeSupergroup).supergroupId)}
            .filter {supergroup ->  supergroup.status is  TdApi.ChatMemberStatusCreator || supergroup.status is TdApi.ChatMemberStatusAdministrator}
            .sortedBy { it.id }
    }

    suspend fun getChannelsInfo(): List<TdApi.SupergroupFullInfo> {
        return getAdminChannelSupergroups().map { supergroup -> getSupergroupFullInfo(supergroup.id) }
    }

    suspend fun getChannelInfoBySuperGroup(): List<TdApi.Chat>{
        val channelSupergroup = getAdminChannelSupergroups()
        val chats = getChats().filterByChannel()
        return chats.filter {
            val chatFilterId = (it.type as TdApi.ChatTypeSupergroup).supergroupId
            chatFilterId == channelSupergroup.firstOrNull{it.id == chatFilterId}?.id
        }.sortedBy { (it.type as TdApi.ChatTypeSupergroup).supergroupId }
    }

    suspend fun getChannelsFullInfo():MutableList<ChannelEntity>{
        val channelsFullInfo:MutableList<ChannelEntity> = mutableListOf()
        getChannelInfoBySuperGroup().forEach { chat -> val channel = ChannelEntity()
                channel.fill(chat)
                channelsFullInfo.add(channel)}
        channelsFullInfo.map { channel -> channel.fill(getSupergroupFullInfo(channel.supergroupId)) }
        return channelsFullInfo
    }

}

fun List<TdApi.Chat>.filterByChannel(): List<TdApi.Chat>{
    return this.filter { it.type is TdApi.ChatTypeSupergroup && (it.type as TdApi.ChatTypeSupergroup).isChannel }
}