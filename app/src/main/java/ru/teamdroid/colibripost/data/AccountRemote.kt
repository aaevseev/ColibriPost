package ru.teamdroid.colibripost.data

import ru.teamdroid.colibripost.domain.user.AccountEntity

interface AccountRemote {
    suspend fun getAccount():AccountEntity
}