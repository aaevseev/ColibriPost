package ru.teamdroid.colibripost.data.account

import ru.teamdroid.colibripost.domain.account.AccountEntity

interface AccountRemote {
    suspend fun getAccount(): AccountEntity
}