package ru.teamdroid.colibripost.remote.channels

import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.remote.core.TelegramClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatsRequests @Inject constructor(
    private val client: TelegramClient
) {

    private suspend fun getChatIds(): LongArray {
        val getChats = TdApi.GetChats(TdApi.ChatListMain(), Long.MAX_VALUE, 0, 50)
        val chats = client.send<TdApi.Chats>(getChats)
        return chats.chatIds
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

    suspend fun getAdminChannelSupergroups(): List<TdApi.Supergroup> {
        return getChats().filterByChannel()
            .map { chat -> getSupergroup((chat.type as TdApi.ChatTypeSupergroup).supergroupId) }
            .filter { supergroup -> supergroup.status is TdApi.ChatMemberStatusCreator || supergroup.status is TdApi.ChatMemberStatusAdministrator }
            .sortedBy { it.id }
    }

    suspend fun getChannelInfoBySuperGroup(
        chatIds: List<Long> = listOf(),
        needAdded: Boolean
    ): List<TdApi.Chat> {
        val channelsSupergroups = getAdminChannelSupergroups()
        val chats = getChats().filterByChannel()

        var channels = chats.filter {
            //фильтр на то что это канал пользователя
            val chatFilterId = (it.type as TdApi.ChatTypeSupergroup).supergroupId
            chatFilterId == channelsSupergroups.firstOrNull { it.id == chatFilterId }?.id
        }

        if (chatIds.isNotEmpty()) {
            channels = channels.filter {
                //фильтр на присутствие канала в бд
                val chatFilterId = it.id
                if (needAdded) chatIds.contains(chatFilterId)
                else !chatIds.contains(chatFilterId)
            }
        }

        return channels.sortedBy { (it.type as TdApi.ChatTypeSupergroup).supergroupId }
    }

    suspend fun getChannelsFullInfo(
        chatIds: List<Long> = listOf(),
        needAdded: Boolean
    ): MutableList<ChannelEntity> {
        val channelsFullInfo: MutableList<ChannelEntity> = mutableListOf()
        getChannelInfoBySuperGroup(chatIds, needAdded).forEach { chat ->
            val channel = ChannelEntity()
            chat.photo?.let { it.small = downloadFiles(it.small) }
            channel.fill(chat)
            channelsFullInfo.add(channel)
        }
        channelsFullInfo.map { channel ->
            channel.fill(getSupergroupFullInfo(channel.supergroupId))
        }
        return channelsFullInfo
    }

    suspend fun downloadFiles(file: TdApi.File): TdApi.File {
        return client.send(
            TdApi.DownloadFile(
                file.id,
                1,
                file.local.downloadOffset,
                file.local.downloadedPrefixSize,
                true
            )
        )
    }

    fun List<TdApi.Chat>.filterByChannel(): List<TdApi.Chat> {
        return this.filter { it.type is TdApi.ChatTypeSupergroup && (it.type as TdApi.ChatTypeSupergroup).isChannel }
    }

}

