package ru.teamdroid.colibripost.data.account.auth

import ru.teamdroid.colibripost.domain.account.auth.AuthRepository
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.remote.core.NetworkHandler

class AuthRepositoryImpl(
    private val authRemote: AuthRemote,
    private val networkHandler: NetworkHandler
) : AuthRepository {

    override suspend fun insertCode(code: String): Either<Failure, None> {
        return if(authRemote.insertCode(code)) Either.Right(None())
        else Either.Left(Failure.InvalidCodeError)
    }
}