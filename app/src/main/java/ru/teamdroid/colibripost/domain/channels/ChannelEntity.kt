package ru.teamdroid.colibripost.domain.channels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.drinkless.td.libcore.telegram.TdApi

@Entity(tableName = "channels_table")
data class ChannelEntity(
    @PrimaryKey
    @ColumnInfo(name = "chat_id")
    var chatId: Long = 0,

    @ColumnInfo(name = "supergroup_id")
    var supergroupId: Int = 0,

    var title: String = "",
    var description: String = "",
    var memberCount: Int = 0,
    var photoPath: String = ""
) {

    fun fill(chat: TdApi.Chat) {
        chatId = chat.id
        title = chat.title
        chat.photo?.let {
            photoPath = it.small.local.path
        }
        supergroupId = (chat.type as TdApi.ChatTypeSupergroup).supergroupId
    }

    fun fill(supergroup: TdApi.SupergroupFullInfo) {
        description = supergroup.description
        memberCount = supergroup.memberCount
    }
}