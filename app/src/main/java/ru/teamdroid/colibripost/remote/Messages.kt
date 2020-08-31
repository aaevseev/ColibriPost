package ru.teamdroid.colibripost.remote

import org.drinkless.td.libcore.telegram.TdApi
import org.drinkless.td.libcore.telegram.TdApi.*
import ru.teamdroid.colibripost.remote.core.TelegramClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Messages @Inject constructor(private val client: TelegramClient) {
    //сообщений за 1 запрос; максимум 100
    var defaultRequest = 50

    suspend fun getMessages(
        chatId: Long,
        fromMessageId: Long = 0,
        limit: Int = defaultRequest
    ): List<TdApi.Message> {
        val messageList = mutableListOf<Message>()
        val lastChatMessage: Message = getLastChatMessage(chatId) ?: return emptyList()
        var startId = if (fromMessageId == 0L) {
            messageList.add(lastChatMessage)
            lastChatMessage.id
        } else {
            fromMessageId
        }
        var messagesRequested = limit
        val oneRequest = if (limit < defaultRequest) limit else defaultRequest
        while (messagesRequested > 0) {
            val getChatHistory = GetChatHistory(chatId, startId, 0, oneRequest, false)
            val request = client.send<TdApi.Messages>(getChatHistory).messages.toList()
            startId = request.last().id
            messageList.addAll(request)
            if (request.size < oneRequest) return messageList
            messagesRequested -= oneRequest
        }

        return messageList
    }

    private suspend fun getLastChatMessage(chatId: Long): Message? {
        val chat = client.send<TdApi.Chat>(TdApi.GetChat(chatId))
        return chat.lastMessage
    }


    suspend fun getMessage(chatId: Long, messageId: Long): TdApi.Message {
        return client.send<Message>(TdApi.GetMessage(chatId, messageId))
    }

    suspend fun sendDelayedMessage(
        chatId: Long,
        content: TdApi.InputMessageContent,
        unixTime: Int
    ): Unit {
        val options = TdApi.SendMessageOptions().apply {
            schedulingState = TdApi.MessageSchedulingStateSendAtDate(unixTime)
            fromBackground = true
        }
        val message = TdApi.SendMessage(chatId, 0, options, null, content)
        client.send<Message>(message)
    }


    suspend fun sendMessage(
        chatId: Long,
        content: TdApi.InputMessageContent,
        delayedUnixTime: Int = 0,
        replyMarkup: ReplyMarkup? = null
    ): Message {
        val options = getMessageOptions(delayedUnixTime)
        val message = TdApi.SendMessage(chatId, 0, options, replyMarkup, content)
        return client.send<Message>(message)
    }

    suspend fun sendAlbum(
        chatId: Long,
        content: Array<InputMessageContent>,
        delayedUnixTime: Int = 0
    ): TdApi.Messages {
        val options = getMessageOptions(delayedUnixTime)
        val album: SendMessageAlbum = SendMessageAlbum(chatId, 0, options, content)
        return client.send(album)
    }

    suspend fun sendMessageAlbum(
        chatId: Long,
        content: Array<TdApi.InputMessageContent>,
        delayedUnixTime: Int = 0
    ): Message {
        val options = getMessageOptions(delayedUnixTime)
        val message = SendMessageAlbum(chatId, 0, options, content)
        return client.send<Message>(message)
    }

    private fun getMessageOptions(delayedUnixTime: Int) = SendMessageOptions().apply {
        if (delayedUnixTime == 0) return@apply
        schedulingState = MessageSchedulingStateSendAtDate(delayedUnixTime)
        fromBackground = true
    }


    fun createSimpleTextMessage(text: String): TdApi.InputMessageText {
        val txt = TdApi.FormattedText(text, null)
        val content = TdApi.InputMessageText(txt, false, false)
        return content
    }

    //эксперементальная функция
    fun createExtendedMessage(): TdApi.InputMessageContent {
        val text = "Тестовое сообщение \nСсылка на гитхаб."
        val list = arrayListOf<Int>()
        val lenght = arrayListOf<Int>()
        list.add(text.indexOf("Ссылка на гитхаб.".also { lenght.add(it.length) }))
        val url =
            "https://github.com/teamdroid/ColibriPost"
        val link1 = TextEntity(list[0], lenght[0], TextEntityTypeTextUrl(url))
        val txt = FormattedText(text, arrayOf(link1))
        val content = InputMessageText(txt, false, false)
        return content
    }
}

