package ru.teamdroid.colibripost.domain.post

import androidx.room.*
import com.google.gson.Gson
import org.drinkless.td.libcore.telegram.TdApi
import java.util.*


@Entity(tableName = "posts_table")
@TypeConverters(FormattedTextConverter::class)
data class PostEntity(
        @PrimaryKey
        var id:Long = 0,
        @ColumnInfo(name = "sender_user_id")
        var senderUserId:Int = 0,
        @ColumnInfo(name = "chat_id")
        var chatId:Long = 0,
        @ColumnInfo(name = "schedule_date")
        var scheduleDate: Int = 0,
        var Day: Int = 0,
        var Month: Int = 0,
        var Year: Int = 0,
        var text: String = "",
        var formatInfo: TdApi.FormattedText? = null,
        var channelPhotoPath: String = ""
){

    fun fill(message: TdApi.Message){

        val calendar = Calendar.getInstance()
        calendar.time = Date((message.schedulingState as TdApi.MessageSchedulingStateSendAtDate).sendDate * 1000L)

        id = message.id
        senderUserId = message.senderUserId
        chatId = message.chatId

        scheduleDate = (message.schedulingState as TdApi.MessageSchedulingStateSendAtDate).sendDate
        Day = calendar.get(Calendar.DAY_OF_MONTH)
        Month = calendar.get(Calendar.MONTH)
        Month = if(Month != 12) Month + 1 else Month - 11
        Year = calendar.get(Calendar.YEAR)

        text = getTextContent(message.content)
    }

    fun getTextContent(content:TdApi.MessageContent): String{

        return when(content){
            is TdApi.MessageText -> content.text.text
            is TdApi.MessagePhoto -> content.caption.text
            is TdApi.MessageVideo -> content.caption.text
            else -> { "" }
        }
    }
}

class FormattedTextConverter{

    @TypeConverter
    fun toString(formatInfo: TdApi.FormattedText?): String?{
        return if(formatInfo == null) null else Gson().toJson(formatInfo)
    }

    @TypeConverter
    fun toFormattedText(string: String?):TdApi.FormattedText?{
        return if(string == null) null
        else {
            Gson().fromJson(string, TdApi.FormattedText::class.java)
        }
    }

}

