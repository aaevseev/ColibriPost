package ru.teamdroid.colibripost.domain.post

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.drinkless.td.libcore.telegram.TdApi


@Entity
data class PostEntity(
        @PrimaryKey
        var id:Long = 0,
        @ColumnInfo(name = "sender_user_id")
        var senderUserId:Int = 0,
        @ColumnInfo(name = "chat_id")
        var chatId:Long = 0,
        @ColumnInfo(name = "schedule_date")
        var scheduleDate: Int = 0,
        var text: TdApi.FormattedText? = null,
        var channelPhotoPath: String = ""
){

    fun fill(message: TdApi.Message){
        id = message.id
        senderUserId = message.senderUserId
        chatId = message.chatId
        scheduleDate = (message.schedulingState as TdApi.MessageSchedulingStateSendAtDate).sendDate
        text = getContent(message.content)
    }

    fun getContent(content:TdApi.MessageContent): TdApi.FormattedText{

        return when(content){
            is TdApi.MessageText -> content.text
            is TdApi.MessagePhoto -> content.caption
            is TdApi.MessageVideo -> content.caption
            else -> { TdApi.FormattedText() }
        }
    }

}