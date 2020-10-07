package ru.teamdroid.colibripost.remote.account

import ru.teamdroid.colibripost.data.AccountRemote
import ru.teamdroid.colibripost.domain.user.AccountEntity
import ru.teamdroid.colibripost.remote.core.NetworkHandler
import javax.inject.Inject

class AccountRemoteImpl @Inject constructor(
    private val accountRequests: AccountRequests
) : AccountRemote {

    override suspend fun getAccount(): AccountEntity {
        return accountRequests.getUserInfo()
    }
}