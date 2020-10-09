package ru.teamdroid.colibripost.domain.account

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.type.None
import javax.inject.Inject

class GetAccount @Inject constructor(
    private val accountRepository: AccountRepository
) : UseCase<AccountEntity, None>() {
    override suspend fun run(params: None) = accountRepository.getAccount()
}