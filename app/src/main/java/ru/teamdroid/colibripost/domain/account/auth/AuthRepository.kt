package ru.teamdroid.colibripost.domain.account.auth

import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None

interface AuthRepository{
    suspend fun insertCode(code: String): Either<Failure, None>
}