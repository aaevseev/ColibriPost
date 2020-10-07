package ru.teamdroid.colibripost.domain.user

import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure

interface AccountRepository {
    suspend fun getAccount(): Either<Failure, AccountEntity>
}