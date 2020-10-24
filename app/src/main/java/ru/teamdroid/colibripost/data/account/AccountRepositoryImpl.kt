package ru.teamdroid.colibripost.data.account

import ru.teamdroid.colibripost.domain.account.AccountEntity
import ru.teamdroid.colibripost.domain.account.AccountRepository
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.onNext
import ru.teamdroid.colibripost.remote.core.NetworkHandler

class AccountRepositoryImpl(
    private val accountRemote: AccountRemote,
    private val accountCache: AccountCache,
    private val networkHandler: NetworkHandler
) : AccountRepository {

    override suspend fun getAccount(): Either<Failure, AccountEntity> {
        val account = accountCache.getAccount()
        return if (networkHandler.isConnected != null)
            Either.Right(accountRemote.getAccount())
                .onNext { account?.let { accountCache.saveAccount(account) } }
        else Either.Right(account!!)
    }
}