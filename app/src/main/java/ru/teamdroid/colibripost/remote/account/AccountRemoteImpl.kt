package ru.teamdroid.colibripost.remote.account

import ru.teamdroid.colibripost.data.account.AccountRemote
import ru.teamdroid.colibripost.domain.account.AccountEntity
import javax.inject.Inject

class AccountRemoteImpl @Inject constructor(
    private val accountRequests: AccountRequests
) : AccountRemote {

    override suspend fun getAccount(): AccountEntity {
        return accountRequests.getUserInfo()
    }
}