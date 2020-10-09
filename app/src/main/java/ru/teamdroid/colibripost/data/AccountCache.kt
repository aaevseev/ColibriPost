package ru.teamdroid.colibripost.data

import ru.teamdroid.colibripost.domain.account.AccountEntity

interface AccountCache {

    fun saveAccount(entity: AccountEntity)

    fun getAccount(): AccountEntity?

}