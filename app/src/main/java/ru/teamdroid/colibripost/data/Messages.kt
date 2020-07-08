package ru.teamdroid.colibripost.data

import android.util.Log
import org.drinkless.td.libcore.telegram.TdApi
import org.drinkless.td.libcore.telegram.TdApi.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Messages @Inject constructor(private val client: TelegramClient) {
    suspend fun getMessages(chatId: Long): List<TdApi.Message> {
        val getChatHistory = TdApi.GetChatHistory(chatId, 0, 0, 100, false)
        val messages = client.send<TdApi.Messages>(getChatHistory)
        return messages.messages.toList()

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


    fun createSimpleTextMessage(text: String): TdApi.InputMessageText {
        val txt = TdApi.FormattedText(text, null)
        val content = TdApi.InputMessageText(txt, false, false)
        return content
    }

    //эксперементальная функция
    fun createExtendedMessage(): TdApi.InputMessageContent {
        val text = "#Реклама\n" +
                "\nПервая половина 2020-ого - то ещё время!\uD83D\uDE31 Проведите вторую половину лучше!\uD83D\uDE09 Освойте востребованную профессию в сфере IT, начните зарабатывать больше и заведите новых друзей!\n" +
                "\n" +
                "9 июля на платформе Skill-Branch стартует новый поток курса Middle Android Developer!\uD83D\uDCA5\n" +
                "\n" +
                "Обновлённый практический курс, который позволит разработчикам продвинуться по карьерной лестнице!\uD83D\uDE80\n" +
                "\n" +
                "Если вы:\n" +
                "• готовы расти профессионально\n" +
                "• хотите прокачать уровень разработки и зарабатывать больше\n" +
                "• претендовать на вакансии крупнейших IT-компаний\n" +
                "\n" +
                "…подайте заявку на обучение здесь \uD83D\uDC49\uD83C\uDFFCперейти на сайт\n" +
                "\n" +
                "Android Middle Developer от Skill-Branch – это:\n" +
                "\uD83D\uDC49 9 месяцев практико-ориентированного обучения\n" +
                "\uD83D\uDC49 300+ часов продвинутого изучения Android-разработки уровня Middle\n" +
                "\uD83D\uDC49 Проектирование архитектуры приложений\n" +
                "\uD83D\uDC49 Актуальные технологии и инструменты, их применение в современной разработке\n" +
                "\uD83D\uDC49 Kotlin, RxJava, Gradle, Mockito, Firebase, Espresso, ML Kit, GraphQL\n" +
                "\uD83D\uDC49 Углубленное изучение Dagger 2 и RxJava 3, а также процессов тестирования Android-приложений\n" +
                "\uD83D\uDC49 Code Review вашего проекта практикующими специалистами\n" +
                "\uD83D\uDC49 Спикеры из Yandex, МТС, Head Hunter и других крупных компаний\n" +
                "\uD83D\uDC49 Сертификат и 2 Android приложения в портфолио, подтверждающие профессиональный уровень\n" +
                "⠀\n" +
                "⚡️ Действует рассрочка до 24 мес.\n" +
                "⚡️ Программа лояльности и скидки\n" +
                "⚡️ Обучение за счёт работодателя\n" +
                "\n" +
                "⚠️ Количество мест ограничено!\n" +
                "\n" +
                "Переходите по ссылке и записывайтесь на обучение сейчас! По промокоду AndroidBroadcast вы получите скидку 3%"
        val list = arrayListOf<Int>()
        val lenght = arrayListOf<Int>()
        list.add(text.indexOf("Первая половина 2020-ого - то ещё время!\uD83D\uDE31 Проведите вторую половину лучше!\uD83D\uDE09 Освойте востребованную профессию в сфере IT, начните зарабатывать больше и заведите новых друзей!".also {
            lenght.add(
                it.length
            )
        }))
        list.add(text.indexOf("курса Middle Android Developer".also { lenght.add(it.length) }))
        list.add(text.indexOf("перейти на сайт".also { lenght.add(it.length) }))
        list.add(text.indexOf("Android Middle Developer от Skill-Branch – это:".also {
            lenght.add(
                it.length
            )
        }))
        list.add(text.indexOf("ссылке".also { lenght.add(it.length) }))
        list.add(text.indexOf("По промокоду AndroidBroadcast вы получите скидку 3%".also {
            lenght.add(
                it.length
            )
        }))
        Log.d("Messages", "createExtendedMessage: $list")
        Log.d("Messages", "createExtendedMessage: $lenght")
        val url =
            "https://skill-branch.ru/middle-android-developer?utm_source=AndroidBroadcastTE-07-07"
        val italic = TextEntity(list[0], lenght[0], TextEntityTypeItalic())
        val link1 = TextEntity(list[1], lenght[1], TextEntityTypeTextUrl(url))
        val link2 = TextEntity(list[2], lenght[2], TextEntityTypeTextUrl(url))
        val bold1 = TextEntity(list[3], lenght[3], TextEntityTypeBold())
        val link3 = TextEntity(list[4], lenght[4], TextEntityTypeTextUrl(url))
        val bold2 = TextEntity(list[5], lenght[5], TextEntityTypeBold())
        val txt = TdApi.FormattedText(text, arrayOf(italic, bold1, bold2, link1, link2, link3))


        val content = TdApi.InputMessageText(txt, false, false)


        return content
    }
}

