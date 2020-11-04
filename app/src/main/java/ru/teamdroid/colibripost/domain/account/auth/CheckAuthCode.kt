package ru.teamdroid.colibripost.domain.account.auth

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import javax.inject.Inject

class CheckAuthCode @Inject constructor(
    private val authRepository: AuthRepository
): UseCase<None, String>(){
    override suspend fun run(params: String) = authRepository.insertCode(params)
}