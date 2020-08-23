package ru.teamdroid.colibripost.domain

import org.drinkless.td.libcore.telegram.TdApi

class ChannelEntity(
    var chatId:Long = 0,
    var supergroupId: Int = 0,
    var title: String = "",
    var description: String = "",
    var memberCount: Int = 0,
    var photo: TdApi.File? = null){

    fun fill(chat: TdApi.Chat){
        chatId = chat.id
        title = chat.title
        photo = chat.photo?.small
        supergroupId = (chat.type as TdApi.ChatTypeSupergroup).supergroupId
    }

    fun fill(supergroup: TdApi.SupergroupFullInfo){
        description = supergroup.description
        memberCount = supergroup.memberCount
    }
}