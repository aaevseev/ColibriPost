package ru.teamdroid.colibripost.domain.account.auth

import ru.teamdroid.colibripost.domain.UseCase
import javax.inject.Inject

class InsertPhoneNumber @Inject constructor(
    private val authRepository: AuthRepository
): UseCase<String, String>(){
    override suspend fun run(params: String) = authRepository.insertPhoneNumber(params)
}