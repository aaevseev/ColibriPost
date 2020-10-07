package ru.teamdroid.colibripost.data

import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.user.AccountEntity

interface AccountCache {

    fun saveAccount(entity: AccountEntity)

    fun getAccount(): AccountEntity?

}