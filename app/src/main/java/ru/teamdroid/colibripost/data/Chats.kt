package ru.teamdroid.colibripost.data

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

    private suspend fun getChannelsIds(): LongArray {
        val getChannels = TdApi.GetChannels(Long.MAX_VALUE, 0, 50)
        val chats = client.send<TdApi.Chats>(getChannels)
        return chats.chatIds
    }

    suspend fun getChats(): List<TdApi.Chat> = getChatIds()
        .map { ids -> getChat(ids) }

    suspend fun getChat(chatId: Long): TdApi.Chat {
        return client.send<TdApi.Chat>(TdApi.GetChat(chatId))
    }

    suspend fun getChannels(): List<TdApi.Channel> {
        return getChats().filter {chat ->  chat.type is TdApi.ChatTypeSupergroup && (chat.type as TdApi.ChatTypeSupergroup).isChannel }
            .map { chat -> getChannel((chat.type as TdApi.ChatTypeSupergroup).supergroupId) }
    }

    suspend fun getChannel(channelId: Int): TdApi.Channel {
        return client.send<TdApi.Channel>(TdApi.GetChannel(channelId))
    }


}