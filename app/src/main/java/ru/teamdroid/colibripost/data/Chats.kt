package ru.teamdroid.colibripost.data

import android.util.Log
import org.drinkless.td.libcore.telegram.TdApi
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
        return client.send<TdApi.Chat>(TdApi.GetChat(chatId))
    }

    suspend fun getChannels(): List<TdApi.SupergroupFullInfo> {
        return getChats().filter {chat ->  chat.type is TdApi.ChatTypeSupergroup && (chat.type as TdApi.ChatTypeSupergroup).isChannel }
            .map { chat -> getSupergroup((chat.type as TdApi.ChatTypeSupergroup).supergroupId) }
            .filter { supergroup ->  supergroup.status is TdApi.ChatMemberStatusAdministrator}
            .map { supergroup -> getSupergroupFullInfo(supergroup.id) }
    }


    suspend fun getSupergroup(superGroupId: Int): TdApi.Supergroup {
        return client.send<TdApi.Supergroup>(TdApi.GetSupergroup(superGroupId))
    }

    suspend fun getSupergroupFullInfo(superGroupId: Int): TdApi.SupergroupFullInfo {
        return client.send<TdApi.SupergroupFullInfo>(TdApi.GetSupergroupFullInfo(superGroupId))
    }


}