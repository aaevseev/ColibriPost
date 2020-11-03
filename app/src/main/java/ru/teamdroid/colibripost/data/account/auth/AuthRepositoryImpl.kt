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

    override suspend fun insertPhoneNumber(phone: String): Either<Failure, String> {
        val response = authRemote.insertPhoneNumber(phone)
        return if(response.contains("Success")) Either.Right(response)
        else if(response.contains("retry after")) {
            val matchResult = Regex("""[0-9]+""").find(response)?.value
            Either.Right(if(matchResult != null) matchResult else "Banned")
        }
        else Either.Left(Failure.ServerError)
    }
}